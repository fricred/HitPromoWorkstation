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
     * Execute the sign-in operation.
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
            username.length < 3 -> AuthResult.Error("Username must be at least 3 characters")
            password.length < 6 -> AuthResult.Error("Password must be at least 6 characters")
            else -> null
        }
    }
}