package net.hitpromo.hitpromoworkstation.presentation.login

import android.content.Context
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
import net.hitpromo.hitpromoworkstation.domain.repository.BadgeAuthRepository
import net.hitpromo.hitpromoworkstation.domain.scanner.ScannerSDKManager
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

    private lateinit var context: Context
    private lateinit var signInUseCase: SignInUseCase
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var userPreferences: UserPreferences
    private lateinit var scannerSDKManager: ScannerSDKManager
    private lateinit var badgeAuthRepository: BadgeAuthRepository
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
        context = mockk(relaxed = true)
        signInUseCase = mockk()
        signOutUseCase = mockk()
        userPreferences = mockk()
        scannerSDKManager = mockk(relaxed = true)
        badgeAuthRepository = mockk(relaxed = true)

        // Default mock behaviors
        every { userPreferences.isLoggedIn } returns flowOf(false)
        every { userPreferences.rememberMe } returns flowOf(false)
        every { userPreferences.userId } returns flowOf(null)
        every { userPreferences.username } returns flowOf(null)
        every { userPreferences.userEmail } returns flowOf(null)
        coEvery { userPreferences.setRememberMe(any()) } returns Unit

        loginViewModel = LoginViewModel(
            context,
            signInUseCase,
            signOutUseCase,
            userPreferences,
            scannerSDKManager,
            badgeAuthRepository
        )
    }

    @Test
    fun `should initialize with unauthenticated state`() = runTest {
        loginViewModel.uiState.test {
            // Skip any initialization states and get the final state
            val initialState = awaitItem()
            // The viewModel may emit multiple states during initialization
            // We should verify the final state after initialization completes
            assertFalse(initialState.isAuthenticated)
            assertEquals(null, initialState.user)
            assertFalse(initialState.rememberMe)
        }
    }

    @Test
    fun `should handle successful sign in`() = runTest {
        // Given
        val username = "testuser"
        val password = "Password123"
        every { signInUseCase(username, password) } returns flowOf(
            AuthResult.Loading,
            AuthResult.Success(mockUser)
        )

        // When & Then
        loginViewModel.uiState.test {
            // Skip initial state from initialization
            skipItems(1)

            // Trigger sign in
            loginViewModel.handleIntent(LoginIntent.SignIn(username, password))

            // Expect loading state
            val loadingState = awaitItem()
            assertTrue("Should be loading", loadingState.isLoading)

            // Expect success state
            val successState = awaitItem()
            assertFalse("Should not be loading", successState.isLoading)
            assertTrue("Should be authenticated", successState.isAuthenticated)
            assertEquals(mockUser, successState.user)
            assertEquals(null, successState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle failed sign in`() = runTest {
        // Given
        val username = "testuser"
        val password = "WrongPassword123"
        val errorMessage = "Invalid credentials"
        every { signInUseCase(username, password) } returns flowOf(
            AuthResult.Loading,
            AuthResult.Error(errorMessage)
        )

        // When & Then
        loginViewModel.uiState.test {
            // Skip initial state from initialization
            skipItems(1)

            // Trigger sign in
            loginViewModel.handleIntent(LoginIntent.SignIn(username, password))

            // Expect loading state
            val loadingState = awaitItem()
            assertTrue("Should be loading", loadingState.isLoading)

            // Expect error state
            val errorState = awaitItem()
            assertFalse("Should not be loading", errorState.isLoading)
            assertFalse("Should not be authenticated", errorState.isAuthenticated)
            assertEquals(errorMessage, errorState.errorMessage)
            assertEquals(null, errorState.user)

            cancelAndIgnoreRemainingEvents()
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

        // When & Then
        loginViewModel.uiState.test {
            // Skip initial state from initialization
            skipItems(1)

            // Trigger sign out
            loginViewModel.handleIntent(LoginIntent.SignOut)

            // Expect loading state
            val loadingState = awaitItem()
            assertTrue("Should be loading", loadingState.isLoading)

            // Expect unauthenticated state
            val unauthenticatedState = awaitItem()
            assertFalse("Should not be loading", unauthenticatedState.isLoading)
            assertFalse("Should not be authenticated", unauthenticatedState.isAuthenticated)
            assertEquals(null, unauthenticatedState.user)
            assertTrue("Should preserve remember me", unauthenticatedState.rememberMe)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should toggle remember me preference`() = runTest {
        // When & Then
        loginViewModel.uiState.test {
            // Skip initial state from initialization
            skipItems(1)

            // Trigger toggle remember me
            loginViewModel.handleIntent(LoginIntent.ToggleRememberMe(true))

            // Expect updated state
            val updatedState = awaitItem()
            assertTrue("Remember me should be true", updatedState.rememberMe)

            cancelAndIgnoreRemainingEvents()
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
        // When & Then
        loginViewModel.uiState.test {
            // Skip initial state from initialization
            skipItems(1)

            // Given - first set an error
            loginViewModel.handleIntent(LoginIntent.ForgotPassword)
            val stateWithError = awaitItem()
            assertTrue("Error message should be set", stateWithError.errorMessage != null)

            // When - clear error
            loginViewModel.handleIntent(LoginIntent.ClearError)
            val clearedState = awaitItem()
            assertEquals(null, clearedState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should initialize with remember me preference from storage`() = runTest {
        // Given
        every { userPreferences.rememberMe } returns flowOf(true)
        every { userPreferences.isLoggedIn } returns flowOf(false)
        every { userPreferences.userId } returns flowOf(null)

        // When
        val testContext = mockk<Context>(relaxed = true)
        val testScannerManager = mockk<ScannerSDKManager>(relaxed = true)
        val testBadgeAuthRepo = mockk<BadgeAuthRepository>(relaxed = true)

        val viewModel = LoginViewModel(
            testContext,
            signInUseCase,
            signOutUseCase,
            userPreferences,
            testScannerManager,
            testBadgeAuthRepo
        )

        // Then
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.rememberMe)
            assertFalse(initialState.isAuthenticated)
        }
    }
}