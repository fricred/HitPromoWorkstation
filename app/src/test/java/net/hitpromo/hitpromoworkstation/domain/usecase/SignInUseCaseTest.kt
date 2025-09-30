package net.hitpromo.hitpromoworkstation.domain.usecase

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.model.UserRole
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SignInUseCase.
 *
 * Tests business logic for user authentication including input validation
 * and repository interaction.
 */
class SignInUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var signInUseCase: SignInUseCase

    private val mockUser = User(
        id = "test-user-id",
        username = "testuser",
        email = "test@hitpromo.net",
        role = UserRole.OPERATOR,
        isActive = true
    )

    @Before
    fun setup() {
        authRepository = mockk()
        signInUseCase = SignInUseCase(authRepository)
    }

    @Test
    fun `should emit loading then success when sign in succeeds`() = runTest {
        // Given
        val username = "testuser"
        val password = "Password123"
        coEvery { authRepository.signIn(username, password) } returns AuthResult.Success(mockUser)

        // When & Then
        signInUseCase(username, password).test {
            assertEquals(AuthResult.Loading, awaitItem())
            assertEquals(AuthResult.Success(mockUser), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `should emit loading then error when sign in fails`() = runTest {
        // Given
        val username = "testuser"
        val password = "WrongPassword123"
        val errorMessage = "Invalid credentials"
        coEvery { authRepository.signIn(username, password) } returns AuthResult.Error(errorMessage)

        // When & Then
        signInUseCase(username, password).test {
            assertEquals(AuthResult.Loading, awaitItem())
            val errorResult = awaitItem() as AuthResult.Error
            assertEquals(errorMessage, errorResult.message)
            awaitComplete()
        }
    }

    @Test
    fun `should emit error for blank username`() = runTest {
        // Given
        val username = ""
        val password = "password123"

        // When & Then
        signInUseCase(username, password).test {
            assertEquals(AuthResult.Loading, awaitItem())
            val errorResult = awaitItem() as AuthResult.Error
            assertEquals("Username cannot be empty", errorResult.message)
            awaitComplete()
        }
    }

    @Test
    fun `should emit error for blank password`() = runTest {
        // Given
        val username = "testuser"
        val password = ""

        // When & Then
        signInUseCase(username, password).test {
            assertEquals(AuthResult.Loading, awaitItem())
            val errorResult = awaitItem() as AuthResult.Error
            assertEquals("Password cannot be empty", errorResult.message)
            awaitComplete()
        }
    }

    @Test
    fun `should emit error for short username`() = runTest {
        // Given
        val username = "ab"
        val password = "password123"

        // When & Then
        signInUseCase(username, password).test {
            assertEquals(AuthResult.Loading, awaitItem())
            val errorResult = awaitItem() as AuthResult.Error
            assertEquals("Username must be at least 3 characters", errorResult.message)
            awaitComplete()
        }
    }

    @Test
    fun `should emit error for short password`() = runTest {
        // Given
        val username = "testuser"
        val password = "Pass1"

        // When & Then
        signInUseCase(username, password).test {
            assertEquals(AuthResult.Loading, awaitItem())
            val errorResult = awaitItem() as AuthResult.Error
            assertEquals("Password must be at least 8 characters and contain uppercase, lowercase, and numbers", errorResult.message)
            awaitComplete()
        }
    }

    @Test
    fun `should trim username before validation and repository call`() = runTest {
        // Given
        val username = "  testuser  "
        val password = "Password123"
        coEvery { authRepository.signIn("testuser", password) } returns AuthResult.Success(mockUser)

        // When & Then
        signInUseCase(username, password).test {
            assertEquals(AuthResult.Loading, awaitItem())
            assertEquals(AuthResult.Success(mockUser), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `should handle repository exception gracefully`() = runTest {
        // Given
        val username = "testuser"
        val password = "Password123"
        val exception = RuntimeException("Network error")
        coEvery { authRepository.signIn(username, password) } throws exception

        // When & Then
        signInUseCase(username, password).test {
            assertEquals(AuthResult.Loading, awaitItem())
            val errorResult = awaitItem() as AuthResult.Error
            assertEquals("An unexpected error occurred", errorResult.message)
            assertEquals(exception, errorResult.cause)
            awaitComplete()
        }
    }
}