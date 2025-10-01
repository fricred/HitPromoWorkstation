package net.hitpromo.hitpromoworkstation.domain.usecase

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetErrorType
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetResult
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for RequestPasswordResetUseCase.
 *
 * Tests business logic for requesting password reset including input validation
 * and repository interaction.
 */
class RequestPasswordResetUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var requestPasswordResetUseCase: RequestPasswordResetUseCase

    @Before
    fun setup() {
        authRepository = mockk()
        requestPasswordResetUseCase = RequestPasswordResetUseCase(authRepository)
    }

    @Test
    fun `should emit Loading then CodeSent when reset succeeds`() = runTest {
        // Given
        val username = "testuser"
        val deliveryDestination = "t***@example.com"
        coEvery { authRepository.requestPasswordReset(username) } returns
            PasswordResetResult.CodeSent(deliveryDestination)

        // When & Then
        requestPasswordResetUseCase(username).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val codeSentResult = awaitItem() as PasswordResetResult.CodeSent
            assertEquals(deliveryDestination, codeSentResult.deliveryDestination)
            awaitComplete()
        }

        // Verify repository was called with correct username
        coVerify { authRepository.requestPasswordReset(username) }
    }

    @Test
    fun `should emit Error for blank username`() = runTest {
        // Given
        val username = ""

        // When & Then
        requestPasswordResetUseCase(username).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("Username cannot be empty", errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_INPUT, errorResult.errorType)
            awaitComplete()
        }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.requestPasswordReset(any()) }
    }

    @Test
    fun `should emit Error for username less than 3 characters`() = runTest {
        // Given
        val username = "ab"

        // When & Then
        requestPasswordResetUseCase(username).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("Username must be at least 3 characters", errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_INPUT, errorResult.errorType)
            awaitComplete()
        }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.requestPasswordReset(any()) }
    }

    @Test
    fun `should trim username before calling repository`() = runTest {
        // Given
        val username = "  testuser  "
        val trimmedUsername = "testuser"
        val deliveryDestination = "t***@example.com"
        coEvery { authRepository.requestPasswordReset(trimmedUsername) } returns
            PasswordResetResult.CodeSent(deliveryDestination)

        // When & Then
        requestPasswordResetUseCase(username).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val codeSentResult = awaitItem() as PasswordResetResult.CodeSent
            assertEquals(deliveryDestination, codeSentResult.deliveryDestination)
            awaitComplete()
        }

        // Verify repository was called with trimmed username
        coVerify { authRepository.requestPasswordReset(trimmedUsername) }
    }

    @Test
    fun `should emit Error when repository throws exception`() = runTest {
        // Given
        val username = "testuser"
        val exception = RuntimeException("Network error")
        coEvery { authRepository.requestPasswordReset(username) } throws exception

        // When & Then
        requestPasswordResetUseCase(username).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("An unexpected error occurred", errorResult.message)
            assertEquals(PasswordResetErrorType.UNKNOWN, errorResult.errorType)
            assertEquals(exception, errorResult.cause)
            awaitComplete()
        }
    }

    @Test
    fun `should propagate repository error result`() = runTest {
        // Given
        val username = "nonexistentuser"
        val errorMessage = "User not found"
        coEvery { authRepository.requestPasswordReset(username) } returns
            PasswordResetResult.Error(errorMessage, PasswordResetErrorType.USER_NOT_FOUND)

        // When & Then
        requestPasswordResetUseCase(username).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals(errorMessage, errorResult.message)
            assertEquals(PasswordResetErrorType.USER_NOT_FOUND, errorResult.errorType)
            awaitComplete()
        }
    }

    @Test
    fun `should emit Error for whitespace-only username`() = runTest {
        // Given
        val username = "   "

        // When & Then
        requestPasswordResetUseCase(username).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals("Username cannot be empty", errorResult.message)
            assertEquals(PasswordResetErrorType.INVALID_INPUT, errorResult.errorType)
            awaitComplete()
        }

        // Verify repository was never called
        coVerify(exactly = 0) { authRepository.requestPasswordReset(any()) }
    }

    @Test
    fun `should handle limit exceeded error from repository`() = runTest {
        // Given
        val username = "testuser"
        val errorMessage = "Too many reset attempts"
        coEvery { authRepository.requestPasswordReset(username) } returns
            PasswordResetResult.Error(errorMessage, PasswordResetErrorType.LIMIT_EXCEEDED)

        // When & Then
        requestPasswordResetUseCase(username).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val errorResult = awaitItem() as PasswordResetResult.Error
            assertEquals(errorMessage, errorResult.message)
            assertEquals(PasswordResetErrorType.LIMIT_EXCEEDED, errorResult.errorType)
            awaitComplete()
        }
    }

    @Test
    fun `should accept valid username with minimum length`() = runTest {
        // Given
        val username = "abc" // Exactly 3 characters
        val deliveryDestination = "a***@example.com"
        coEvery { authRepository.requestPasswordReset(username) } returns
            PasswordResetResult.CodeSent(deliveryDestination)

        // When & Then
        requestPasswordResetUseCase(username).test {
            assertEquals(PasswordResetResult.Loading, awaitItem())
            val codeSentResult = awaitItem() as PasswordResetResult.CodeSent
            assertEquals(deliveryDestination, codeSentResult.deliveryDestination)
            awaitComplete()
        }

        // Verify repository was called
        coVerify { authRepository.requestPasswordReset(username) }
    }
}
