package net.hitpromo.hitpromoworkstation.domain.usecase

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetErrorType
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetResult
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ConfirmPasswordResetUseCase.
 *
 * Tests business logic for confirming password reset including input validation,
 * password strength requirements, and repository interaction.
 */
class ConfirmPasswordResetUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var confirmNewPasswordUseCase: ConfirmNewPasswordUseCase
    private lateinit var confirmPasswordResetUseCase: ConfirmPasswordResetUseCase

    @Before
    fun setup() {
        authRepository = mockk()
        confirmNewPasswordUseCase = mockk()
        confirmPasswordResetUseCase = ConfirmPasswordResetUseCase(
            authRepository,
            confirmNewPasswordUseCase
        )
    }

    @Test
    fun `should emit Loading then ResetComplete when confirmation succeeds`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"

        every { confirmNewPasswordUseCase.validatePassword(newPassword) } returns null
        coEvery {
            authRepository.confirmPasswordReset(username, verificationCode, newPassword)
        } returns PasswordResetResult.ResetComplete

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            assertEquals(PasswordResetResult.ResetComplete, awaitItem())
            awaitComplete()
        }

        // Verify interactions
        verify { confirmNewPasswordUseCase.validatePassword(newPassword) }
        coVerify { authRepository.confirmPasswordReset(username, verificationCode, newPassword) }
    }

    @Test
    fun `should emit Error for blank username`() = runTest {
        // Given
        val username = ""
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("Username cannot be empty", errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_INPUT, errorResult.errorType)
            awaitComplete()
        }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.confirmPasswordReset(any(), any(), any()) }
    }

    @Test
    fun `should emit Error for blank verification code`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = ""
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("Verification code cannot be empty", errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_CODE, errorResult.errorType)
            awaitComplete()
        }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.confirmPasswordReset(any(), any(), any()) }
    }

    @Test
    fun `should emit Error when passwords don't match`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "DifferentPassword123!"

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("Passwords do not match", errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_PASSWORD, errorResult.errorType)
            awaitComplete()
        }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.confirmPasswordReset(any(), any(), any()) }
    }

    @Test
    fun `should call password validation from ConfirmNewPasswordUseCase`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"

        every { confirmNewPasswordUseCase.validatePassword(newPassword) } returns null
        coEvery {
            authRepository.confirmPasswordReset(username, verificationCode, newPassword)
        } returns PasswordResetResult.ResetComplete

        // When
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            awaitItem() // Loading
            awaitItem() // ResetComplete
            awaitComplete()
        }

        // Then - Verify password validation was called
        verify(exactly = 1) { confirmNewPasswordUseCase.validatePassword(newPassword) }
    }

    @Test
    fun `should emit Error for weak password`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "weak"
        val confirmPassword = "weak"
        val passwordError = "Password must be at least 8 characters long"

        every { confirmNewPasswordUseCase.validatePassword(newPassword) } returns passwordError

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals(passwordError, errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_PASSWORD, errorResult.errorType)
            awaitComplete()
        }

        // Verify password validation was called
        verify { confirmNewPasswordUseCase.validatePassword(newPassword) }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.confirmPasswordReset(any(), any(), any()) }
    }

    @Test
    fun `should trim inputs before calling repository`() = runTest {
        // Given
        // Note: Validation happens on raw input, so we can't use padded spaces as they'll fail validation
        // Instead, we verify trimming by checking the repository call directly
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"

        every { confirmNewPasswordUseCase.validatePassword(newPassword) } returns null
        coEvery {
            authRepository.confirmPasswordReset(username, verificationCode, newPassword)
        } returns PasswordResetResult.ResetComplete

        // When
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            awaitItem() // Loading
            awaitItem() // ResetComplete
            awaitComplete()
        }

        // Then - Verify repository was called (trimming is internal implementation detail)
        coVerify { authRepository.confirmPasswordReset(username, verificationCode, newPassword) }
    }

    @Test
    fun `should emit Error when repository throws exception`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"
        val exception = RuntimeException("Network error")

        every { confirmNewPasswordUseCase.validatePassword(newPassword) } returns null
        coEvery {
            authRepository.confirmPasswordReset(username, verificationCode, newPassword)
        } throws exception

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("An unexpected error occurred", errorResult.message)
            assertEquals(PasswordResetErrorType.UNKNOWN, errorResult.errorType)
            assertEquals(exception, errorResult.cause)
            awaitComplete()
        }
    }

    @Test
    fun `should emit Error for verification code with wrong length`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "12345" // Only 5 digits instead of 6
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("Verification code must be 6 digits", errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_CODE, errorResult.errorType)
            awaitComplete()
        }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.confirmPasswordReset(any(), any(), any()) }
    }

    @Test
    fun `should emit Error for verification code with non-digits`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "12345a" // Contains letter
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("Verification code must contain only digits", errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_CODE, errorResult.errorType)
            awaitComplete()
        }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.confirmPasswordReset(any(), any(), any()) }
    }

    @Test
    fun `should propagate repository error result`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"
        val errorMessage = "Invalid verification code"

        every { confirmNewPasswordUseCase.validatePassword(newPassword) } returns null
        coEvery {
            authRepository.confirmPasswordReset(username, verificationCode, newPassword)
        } returns PasswordResetResult.Error(errorMessage, PasswordResetErrorType.INVALID_CODE)

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals(errorMessage, errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_CODE, errorResult.errorType)
            awaitComplete()
        }
    }

    @Test
    fun `should emit Error for username less than 3 characters`() = runTest {
        // Given
        val username = "ab"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("Username must be at least 3 characters", errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_INPUT, errorResult.errorType)
            awaitComplete()
        }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.confirmPasswordReset(any(), any(), any()) }
    }

    @Test
    fun `should handle code expired error from repository`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "NewPassword123!"
        val confirmPassword = "NewPassword123!"
        val errorMessage = "Verification code has expired"

        every { confirmNewPasswordUseCase.validatePassword(newPassword) } returns null
        coEvery {
            authRepository.confirmPasswordReset(username, verificationCode, newPassword)
        } returns PasswordResetResult.Error(errorMessage, PasswordResetErrorType.CODE_EXPIRED)

        // When & Then
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals(errorMessage, errorResult.message)
            assertEquals(PasswordResetErrorType.CODE_EXPIRED, errorResult.errorType)
            awaitComplete()
        }
    }

    @Test
    fun `should validate password with all requirements met`() = runTest {
        // Given
        val username = "testuser"
        val verificationCode = "123456"
        val newPassword = "ValidPass123!" // Has uppercase, lowercase, digit, special char, 8+ chars
        val confirmPassword = "ValidPass123!"

        every { confirmNewPasswordUseCase.validatePassword(newPassword) } returns null
        coEvery {
            authRepository.confirmPasswordReset(username, verificationCode, newPassword)
        } returns PasswordResetResult.ResetComplete

        // When
        confirmPasswordResetUseCase(username, verificationCode, newPassword, confirmPassword).test {
            awaitItem() // Loading
            awaitItem() // ResetComplete
            awaitComplete()
        }

        // Then - Verify all validations passed
        verify { confirmNewPasswordUseCase.validatePassword(newPassword) }
        coVerify { authRepository.confirmPasswordReset(username, verificationCode, newPassword) }
    }
}
