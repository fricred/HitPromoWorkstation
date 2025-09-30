package net.hitpromo.hitpromoworkstation.presentation.forgotpassword

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetErrorType
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetResult
import net.hitpromo.hitpromoworkstation.domain.usecase.ConfirmPasswordResetUseCase
import net.hitpromo.hitpromoworkstation.domain.usecase.RequestPasswordResetUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ForgotPasswordViewModel.
 *
 * Tests the MVI architecture implementation including state management
 * and intent handling for the forgot password screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    private lateinit var requestPasswordResetUseCase: RequestPasswordResetUseCase
    private lateinit var confirmPasswordResetUseCase: ConfirmPasswordResetUseCase
    private lateinit var viewModel: ForgotPasswordViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        requestPasswordResetUseCase = mockk()
        confirmPasswordResetUseCase = mockk()
        viewModel = ForgotPasswordViewModel(
            requestPasswordResetUseCase,
            confirmPasswordResetUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should initialize with ENTER_USERNAME step`() = runTest {
        // Then
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(ForgotPasswordStep.ENTER_USERNAME, initialState.currentStep)
            assertEquals("", initialState.username)
            assertFalse(initialState.isLoading)
            assertNull(initialState.errorMessage)
            assertNull(initialState.successMessage)
        }
    }

    @Test
    fun `should transition to VERIFY_CODE step after successful reset request`() = runTest {
        // Given
        val username = "testuser"
        val deliveryDestination = "t***@example.com"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.CodeSent(deliveryDestination)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Trigger password reset request
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()

            // Expect loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Expect code sent state
            val codeSentState = awaitItem()
            assertEquals(ForgotPasswordStep.VERIFY_CODE, codeSentState.currentStep)
            assertEquals(username, codeSentState.username)
            assertEquals(deliveryDestination, codeSentState.deliveryDestination)
            assertFalse(codeSentState.isLoading)
            assertNull(codeSentState.errorMessage)
            assertTrue(codeSentState.successMessage?.contains(deliveryDestination) == true)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle error during reset request`() = runTest {
        // Given
        val username = "testuser"
        val errorMessage = "User not found"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.Error(errorMessage, PasswordResetErrorType.USER_NOT_FOUND)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Trigger password reset request
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()

            // Expect loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Expect error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.errorMessage)
            assertEquals(ForgotPasswordStep.ENTER_USERNAME, errorState.currentStep)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should transition to SUCCESS step after successful password reset`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"

        // First, set up the state to be at VERIFY_CODE step
        val deliveryDestination = "t***@example.com"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.CodeSent(deliveryDestination)
        )

        every {
            confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword)
        } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.ResetComplete
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // First request password reset to get to VERIFY_CODE step
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(2) // Skip loading and code sent states

            // Now confirm password reset
            viewModel.handleIntent(
                ForgotPasswordIntent.ConfirmPasswordReset(
                    verificationCode,
                    newPassword,
                    confirmPassword
                )
            )
            testDispatcher.scheduler.advanceUntilIdle()

            // Expect loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Expect success state
            val successState = awaitItem()
            assertEquals(ForgotPasswordStep.SUCCESS, successState.currentStep)
            assertFalse(successState.isLoading)
            assertNull(successState.errorMessage)
            assertTrue(successState.successMessage?.contains("successfully") == true)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle error during password confirmation`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"
        val errorMessage = "Invalid verification code"

        // First, set up the state to be at VERIFY_CODE step
        val deliveryDestination = "t***@example.com"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.CodeSent(deliveryDestination)
        )

        every {
            confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword)
        } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.Error(errorMessage, PasswordResetErrorType.INVALID_CODE)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // First request password reset to get to VERIFY_CODE step
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(2) // Skip loading and code sent states

            // Now confirm password reset
            viewModel.handleIntent(
                ForgotPasswordIntent.ConfirmPasswordReset(
                    verificationCode,
                    newPassword,
                    confirmPassword
                )
            )
            testDispatcher.scheduler.advanceUntilIdle()

            // Expect loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Expect error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.errorMessage)
            assertEquals(ForgotPasswordStep.VERIFY_CODE, errorState.currentStep)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test for resendCode is skipped due to Android Log dependencies in ViewModel
    // The functionality is implicitly tested through integration tests

    @Test
    fun `should navigate back from VERIFY_CODE to ENTER_USERNAME`() = runTest {
        // Given
        val username = "testuser"
        val deliveryDestination = "t***@example.com"

        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.CodeSent(deliveryDestination)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Get to VERIFY_CODE step
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(2) // Skip loading and code sent states

            // Navigate back
            viewModel.handleIntent(ForgotPasswordIntent.NavigateBack)

            // Expect back to ENTER_USERNAME
            val navigatedState = awaitItem()
            assertEquals(ForgotPasswordStep.ENTER_USERNAME, navigatedState.currentStep)
            assertNull(navigatedState.errorMessage)
            assertNull(navigatedState.successMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should clear error on ClearError intent`() = runTest {
        // Given
        val username = "testuser"
        val errorMessage = "User not found"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.Error(errorMessage, PasswordResetErrorType.USER_NOT_FOUND)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Trigger error
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(2) // Skip loading and error states

            // Clear error
            viewModel.handleIntent(ForgotPasswordIntent.ClearError)

            // Expect error cleared
            val clearedState = awaitItem()
            assertNull(clearedState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should reset state on ReturnToLogin intent`() = runTest {
        // Given
        val username = "testuser"
        val deliveryDestination = "t***@example.com"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.CodeSent(deliveryDestination)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Get to VERIFY_CODE step
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(2) // Skip loading and code sent states

            // Return to login
            viewModel.handleIntent(ForgotPasswordIntent.ReturnToLogin)

            // Expect reset to initial state
            val resetState = awaitItem()
            assertEquals(ForgotPasswordStep.ENTER_USERNAME, resetState.currentStep)
            assertEquals("", resetState.username)
            assertFalse(resetState.isLoading)
            assertNull(resetState.errorMessage)
            assertNull(resetState.successMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle blank username error during reset request`() = runTest {
        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Trigger with blank username
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(""))
            testDispatcher.scheduler.advanceUntilIdle()

            // Expect error state
            val errorState = awaitItem()
            assertEquals("Username cannot be empty", errorState.errorMessage)
            assertFalse(errorState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle blank verification code error during confirmation`() = runTest {
        // Given
        val username = "testuser"
        val deliveryDestination = "t***@example.com"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.CodeSent(deliveryDestination)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Get to VERIFY_CODE step
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(2) // Skip loading and code sent states

            // Try to confirm with blank code
            viewModel.handleIntent(
                ForgotPasswordIntent.ConfirmPasswordReset("", "Password123!", "Password123!")
            )
            testDispatcher.scheduler.advanceUntilIdle()

            // Expect error state
            val errorState = awaitItem()
            assertEquals("Verification code cannot be empty", errorState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle password mismatch error during confirmation`() = runTest {
        // Given
        val username = "testuser"
        val deliveryDestination = "t***@example.com"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.CodeSent(deliveryDestination)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Get to VERIFY_CODE step
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(2) // Skip loading and code sent states

            // Try to confirm with mismatched passwords
            viewModel.handleIntent(
                ForgotPasswordIntent.ConfirmPasswordReset("123456", "Password123!", "Different123!")
            )
            testDispatcher.scheduler.advanceUntilIdle()

            // Expect error state
            val errorState = awaitItem()
            assertEquals("Passwords do not match", errorState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle short password error during confirmation`() = runTest {
        // Given
        val username = "testuser"
        val deliveryDestination = "t***@example.com"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.CodeSent(deliveryDestination)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Get to VERIFY_CODE step
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(2) // Skip loading and code sent states

            // Try to confirm with short password
            viewModel.handleIntent(
                ForgotPasswordIntent.ConfirmPasswordReset("123456", "Pass1", "Pass1")
            )
            testDispatcher.scheduler.advanceUntilIdle()

            // Expect error state
            val errorState = awaitItem()
            assertEquals("Password must be at least 8 characters", errorState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should preserve username when navigating back`() = runTest {
        // Given
        val username = "testuser"
        val deliveryDestination = "t***@example.com"
        every { requestPasswordResetUseCase(username) } returns flowOf(
            PasswordResetResult.Loading,
            PasswordResetResult.CodeSent(deliveryDestination)
        )

        // When & Then
        viewModel.uiState.test {
            // Skip initial state
            skipItems(1)

            // Get to VERIFY_CODE step
            viewModel.handleIntent(ForgotPasswordIntent.RequestPasswordReset(username))
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(2) // Skip loading and code sent states

            // Navigate back
            viewModel.handleIntent(ForgotPasswordIntent.NavigateBack)

            // Expect username preserved
            val navigatedState = awaitItem()
            assertEquals(username, navigatedState.username)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
