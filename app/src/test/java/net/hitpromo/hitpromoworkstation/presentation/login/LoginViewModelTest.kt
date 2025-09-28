package net.hitpromo.hitpromoworkstation.presentation.login

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.hitpromo.hitpromoworkstation.data.local.UserPreferences
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.model.UserRole
import net.hitpromo.hitpromoworkstation.domain.usecase.SignInUseCase
import net.hitpromo.hitpromoworkstation.domain.usecase.SignOutUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for LoginViewModel.
 *
 * Tests the MVI architecture implementation including state management
 * and intent handling for the login screen.
 */
class LoginViewModelTest {

    private lateinit var signInUseCase: SignInUseCase
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var userPreferences: UserPreferences
    private lateinit var loginViewModel: LoginViewModel

    private val mockUser = User(
        id = "test-user-id",
        username = "testuser",
        email = "test@hitpromo.net",
        role = UserRole.OPERATOR,
        isActive = true
    )

    @Before
    fun setup() {
        signInUseCase = mockk()
        signOutUseCase = mockk()
        userPreferences = mockk()

        // Default mock behaviors
        every { userPreferences.isLoggedIn } returns flowOf(false)
        every { userPreferences.rememberMe } returns flowOf(false)
        every { userPreferences.userId } returns flowOf(null)
        every { userPreferences.username } returns flowOf(null)
        every { userPreferences.userEmail } returns flowOf(null)
        coEvery { userPreferences.setRememberMe(any()) } returns Unit

        loginViewModel = LoginViewModel(signInUseCase, signOutUseCase, userPreferences)
    }

    @Test
    fun `should initialize with unauthenticated state`() = runTest {
        loginViewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isAuthenticated)
            assertFalse(initialState.isLoading)
            assertEquals(null, initialState.user)
            assertEquals(null, initialState.errorMessage)
            assertFalse(initialState.rememberMe)
        }
    }

    @Test
    fun `should handle successful sign in`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        every { signInUseCase(username, password) } returns flowOf(
            AuthResult.Loading,
            AuthResult.Success(mockUser)
        )

        // When
        loginViewModel.handleIntent(LoginIntent.SignIn(username, password))

        // Then
        loginViewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertFalse(loadingState.isAuthenticated)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertTrue(successState.isAuthenticated)
            assertEquals(mockUser, successState.user)
            assertEquals(null, successState.errorMessage)
        }
    }

    @Test
    fun `should handle failed sign in`() = runTest {
        // Given
        val username = "testuser"
        val password = "wrongpassword"
        val errorMessage = "Invalid credentials"
        every { signInUseCase(username, password) } returns flowOf(
            AuthResult.Loading,
            AuthResult.Error(errorMessage)
        )

        // When
        loginViewModel.handleIntent(LoginIntent.SignIn(username, password))

        // Then
        loginViewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertFalse(errorState.isAuthenticated)
            assertEquals(errorMessage, errorState.errorMessage)
            assertEquals(null, errorState.user)
        }
    }

    @Test
    fun `should handle successful sign out`() = runTest {
        // Given
        every { signOutUseCase() } returns flowOf(
            AuthResult.Loading,
            AuthResult.Success(Unit)
        )
        every { userPreferences.rememberMe } returns flowOf(true)

        // When
        loginViewModel.handleIntent(LoginIntent.SignOut)

        // Then
        loginViewModel.uiState.test {
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val unauthenticatedState = awaitItem()
            assertFalse(unauthenticatedState.isLoading)
            assertFalse(unauthenticatedState.isAuthenticated)
            assertEquals(null, unauthenticatedState.user)
            assertTrue(unauthenticatedState.rememberMe) // Should preserve remember me preference
        }
    }

    @Test
    fun `should toggle remember me preference`() = runTest {
        // When
        loginViewModel.handleIntent(LoginIntent.ToggleRememberMe(true))

        // Then
        loginViewModel.uiState.test {
            val updatedState = awaitItem()
            assertTrue(updatedState.rememberMe)
        }
    }

    @Test
    fun `should handle forgot password intent`() = runTest {
        // When
        loginViewModel.handleIntent(LoginIntent.ForgotPassword)

        // Then
        loginViewModel.uiState.test {
            val updatedState = awaitItem()
            assertEquals(
                "Please contact your system administrator for password reset assistance.",
                updatedState.errorMessage
            )
        }
    }

    @Test
    fun `should clear error message`() = runTest {
        // Given - first set an error
        loginViewModel.handleIntent(LoginIntent.ForgotPassword)

        // When
        loginViewModel.handleIntent(LoginIntent.ClearError)

        // Then
        loginViewModel.uiState.test {
            // Skip the error state
            awaitItem()
            val clearedState = awaitItem()
            assertEquals(null, clearedState.errorMessage)
        }
    }

    @Test
    fun `should initialize with remember me preference from storage`() = runTest {
        // Given
        every { userPreferences.rememberMe } returns flowOf(true)
        every { userPreferences.isLoggedIn } returns flowOf(false)
        every { userPreferences.userId } returns flowOf(null)

        // When
        val viewModel = LoginViewModel(signInUseCase, signOutUseCase, userPreferences)

        // Then
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.rememberMe)
            assertFalse(initialState.isAuthenticated)
        }
    }
}