package net.hitpromo.hitpromoworkstation.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for signing in a user.
 *
 * This use case handles the business logic for user authentication,
 * including input validation and coordinating with the repository.
 */
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Execute the sign-in operation with username and password (legacy).
     *
     * @param username The user's username
     * @param password The user's password
     * @return Flow of AuthResult indicating the progress and result of the operation
     */
    operator fun invoke(username: String, password: String): Flow<AuthResult<User>> = flow {
        try {
            emit(AuthResult.Loading)

            // Validate input
            val validationResult = validateInput(username, password)
            if (validationResult != null) {
                emit(validationResult)
                return@flow
            }

            // Attempt sign-in
            val result = authRepository.signIn(username.trim(), password)
            emit(result)

        } catch (e: Exception) {
            emit(AuthResult.Error("An unexpected error occurred", e))
        }
    }

    /**
     * Execute badge-based sign-in operation.
     *
     * The badge ID is used as the username, and a fixed token is used as the password
     * for authentication with AWS Cognito.
     *
     * @param badgeId The scanned badge ID
     * @return Flow of AuthResult indicating the progress and result of the operation
     */
    fun signInWithBadge(badgeId: String): Flow<AuthResult<User>> = flow {
        try {
            emit(AuthResult.Loading)

            // Validate badge ID
            val validationResult = validateBadgeId(badgeId)
            if (validationResult != null) {
                emit(validationResult)
                return@flow
            }

            // Use badge ID as both username and password for now
            // In a production system, you would have a backend that validates the badge
            // and returns appropriate credentials
            val result = authRepository.signIn(badgeId.trim(), badgeId.trim())
            emit(result)

        } catch (e: Exception) {
            emit(AuthResult.Error("Badge authentication failed: ${e.message}", e))
        }
    }

    /**
     * Validate badge ID input.
     *
     * @param badgeId The badge ID to validate
     * @return AuthResult.Error if validation fails, null if valid
     */
    private fun validateBadgeId(badgeId: String): AuthResult.Error? {
        return when {
            badgeId.isBlank() -> AuthResult.Error("Badge ID cannot be empty")
            badgeId.length < MIN_BADGE_ID_LENGTH ->
                AuthResult.Error("Invalid badge ID format")
            else -> null
        }
    }

    /**
     * Validate the current authentication session.
     *
     * Checks if the user has a valid session with Amplify Auth and restores user data if valid.
     *
     * @return Flow of AuthResult indicating the session validation result
     */
    fun validateSession(): Flow<AuthResult<User>> = flow {
        try {
            emit(AuthResult.Loading)

            // Validate session with repository
            val isValid = authRepository.validateSession()

            if (isValid) {
                // Session is valid, get current user
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    emit(AuthResult.Success(user))
                } else {
                    // Session valid but no user data, try refreshing
                    val refreshResult = authRepository.refreshSession()
                    emit(refreshResult)
                }
            } else {
                emit(AuthResult.Error("Session expired or invalid"))
            }

        } catch (e: Exception) {
            emit(AuthResult.Error("Session validation failed: ${e.message}", e))
        }
    }

    /**
     * Validate sign-in input parameters.
     *
     * @param username The username to validate
     * @param password The password to validate
     * @return AuthResult.Error if validation fails, null if valid
     */
    private fun validateInput(username: String, password: String): AuthResult.Error? {
        return when {
            username.isBlank() -> AuthResult.Error("Username cannot be empty")
            password.isBlank() -> AuthResult.Error("Password cannot be empty")
            username.length < MIN_USERNAME_LENGTH ->
                AuthResult.Error("Username must be at least $MIN_USERNAME_LENGTH characters")
            !isPasswordValid(password) ->
                AuthResult.Error("Password must be at least $MIN_PASSWORD_LENGTH characters and contain uppercase, lowercase, and numbers")
            else -> null
        }
    }

    /**
     * Validate password complexity requirements.
     *
     * Password must be at least 8 characters and contain:
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     *
     * @param password The password to validate
     * @return true if password meets requirements, false otherwise
     */
    private fun isPasswordValid(password: String): Boolean {
        if (password.length < MIN_PASSWORD_LENGTH) {
            return false
        }

        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }

        return hasUppercase && hasLowercase && hasDigit
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MIN_PASSWORD_LENGTH = 8
        private const val MIN_BADGE_ID_LENGTH = 3
    }
}