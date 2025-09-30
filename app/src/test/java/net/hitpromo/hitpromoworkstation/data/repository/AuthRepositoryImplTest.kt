package net.hitpromo.hitpromoworkstation.data.repository

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.hitpromo.hitpromoworkstation.data.local.UserPreferences
import net.hitpromo.hitpromoworkstation.data.remote.CognitoAuthDataSource
import net.hitpromo.hitpromoworkstation.data.remote.dto.CognitoUserDto
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.model.AuthenticationState
import net.hitpromo.hitpromoworkstation.domain.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AuthRepositoryImpl.
 *
 * Tests the repository layer that coordinates between data sources
 * and handles authentication state management.
 */
class AuthRepositoryImplTest {

    private lateinit var cognitoDataSource: CognitoAuthDataSource
    private lateinit var userPreferences: UserPreferences
    private lateinit var authRepository: AuthRepositoryImpl

    private val mockCognitoUser = CognitoUserDto(
        username = "testuser",
        userId = "test-user-id",
        email = "test@hitpromo.net",
        attributes = mapOf(
            "custom:role" to "OPERATOR",
            "custom:is_active" to "true"
        )
    )

    @Before
    fun setup() {
        cognitoDataSource = mockk()
        userPreferences = mockk()

        // Default mock behaviors
        every { userPreferences.isLoggedIn } returns flowOf(false)
        every { userPreferences.userId } returns flowOf(null)
        every { userPreferences.username } returns flowOf(null)
        every { userPreferences.userEmail } returns flowOf(null)
        every { userPreferences.userRole } returns flowOf(null)
        coEvery { userPreferences.saveUserSession(any(), any(), any(), any(), any()) } returns Unit
        coEvery { userPreferences.clearUserSession() } returns Unit

        authRepository = AuthRepositoryImpl(cognitoDataSource, userPreferences)
    }

    @Test
    fun `should emit authenticated state on successful sign in`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        coEvery { cognitoDataSource.signIn(username, password) } returns AuthResult.Success(mockCognitoUser)

        // When
        val result = authRepository.signIn(username, password)

        // Then
        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).data
        assertEquals("testuser", user.username)
        assertEquals("test@hitpromo.net", user.email)
        assertEquals(UserRole.OPERATOR, user.role)

        // Verify that authentication state is updated
        authRepository.authenticationState.test {
            val state = awaitItem()
            assertTrue(state is AuthenticationState.Authenticated)
            assertEquals(user, (state as AuthenticationState.Authenticated).user)
        }

        // Verify that user session is saved
        coVerify {
            userPreferences.saveUserSession(
                userId = "test-user-id",
                username = "testuser",
                email = "test@hitpromo.net",
                role = "OPERATOR"
            )
        }
    }

    @Test
    fun `should emit error state on failed sign in`() = runTest {
        // Given
        val username = "testuser"
        val password = "wrongpassword"
        val errorMessage = "Invalid credentials"
        coEvery { cognitoDataSource.signIn(username, password) } returns AuthResult.Error(errorMessage)

        // When
        val result = authRepository.signIn(username, password)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals(errorMessage, (result as AuthResult.Error).message)

        // Verify that authentication state shows error
        authRepository.authenticationState.test {
            val state = awaitItem()
            assertTrue(state is AuthenticationState.Error)
            assertEquals(errorMessage, (state as AuthenticationState.Error).message)
        }
    }

    @Test
    fun `should clear authentication state on sign out`() = runTest {
        // Given
        coEvery { cognitoDataSource.signOut() } returns AuthResult.Success(Unit)

        // When
        val result = authRepository.signOut()

        // Then
        assertTrue(result is AuthResult.Success)

        // Verify that authentication state is cleared
        authRepository.authenticationState.test {
            val state = awaitItem()
            assertTrue(state is AuthenticationState.Unauthenticated)
        }

        // Verify that local session is cleared
        coVerify { userPreferences.clearUserSession() }
    }

    @Test
    fun `should return true when user is authenticated`() = runTest {
        // Given
        coEvery { cognitoDataSource.validateSession() } returns true
        every { userPreferences.isLoggedIn } returns flowOf(true)

        // When
        val isAuthenticated = authRepository.isAuthenticated()

        // Then
        assertTrue(isAuthenticated)
    }

    @Test
    fun `should return false when user is not authenticated`() = runTest {
        // Given
        coEvery { cognitoDataSource.validateSession() } returns false
        every { userPreferences.isLoggedIn } returns flowOf(false)

        // When
        val isAuthenticated = authRepository.isAuthenticated()

        // Then
        assertFalse(isAuthenticated)
    }

    @Test
    fun `should validate session against both remote and local storage`() = runTest {
        // Given
        coEvery { cognitoDataSource.validateSession() } returns true
        every { userPreferences.isLoggedIn } returns flowOf(true)
        every { userPreferences.userId } returns flowOf("test-user-id")

        // When
        val isValid = authRepository.validateSession()

        // Then
        assertTrue(isValid)
    }

    @Test
    fun `should return false when session validation fails`() = runTest {
        // Given
        coEvery { cognitoDataSource.validateSession() } returns false

        // When
        val isValid = authRepository.validateSession()

        // Then
        assertFalse(isValid)
    }

    @Test
    fun `should refresh session successfully`() = runTest {
        // Given
        coEvery { cognitoDataSource.refreshSession() } returns AuthResult.Success(mockCognitoUser)

        // When
        val result = authRepository.refreshSession()

        // Then
        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).data
        assertEquals("testuser", user.username)

        // Verify that authentication state is updated
        authRepository.authenticationState.test {
            val state = awaitItem()
            assertTrue(state is AuthenticationState.Authenticated)
        }
    }

    @Test
    fun `should clear session when refresh fails`() = runTest {
        // Given
        val errorMessage = "Session expired"
        coEvery { cognitoDataSource.refreshSession() } returns AuthResult.Error(errorMessage)

        // When
        val result = authRepository.refreshSession()

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals(errorMessage, (result as AuthResult.Error).message)

        // Verify that authentication state shows error and local session is cleared
        authRepository.authenticationState.test {
            val state = awaitItem()
            assertTrue(state is AuthenticationState.Error)
        }

        coVerify { userPreferences.clearUserSession() }
    }

    // Password Reset Tests
    // Note: Repository-level password reset tests are skipped due to Android Log
    // dependencies. These operations are thoroughly tested at the use case level.
    // See RequestPasswordResetUseCaseTest and ConfirmPasswordResetUseCaseTest.
}