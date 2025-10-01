package net.hitpromo.hitpromoworkstation.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetErrorType
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetResult
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for requesting a password reset.
 *
 * This use case handles the business logic for initiating the password reset flow,
 * including username validation and coordinating with the repository to send
 * the verification code.
 */
class RequestPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Execute the password reset request operation.
     *
     * @param username The username for which to request password reset
     * @return Flow of PasswordResetResult indicating the progress and result of the operation
     */
    operator fun invoke(username: String): Flow<PasswordResetResult> = flow {
        try {
            emit(PasswordResetResult.Loading)

            // Validate input
            val validationResult = validateUsername(username)
            if (validationResult != null) {
                emit(validationResult)
                return@flow
            }

            // Request password reset
            val result = authRepository.requestPasswordReset(username.trim())
            emit(result)

        } catch (e: Exception) {
            emit(
                PasswordResetResult.Error(
                    message = "An unexpected error occurred",
                    errorType = PasswordResetErrorType.UNKNOWN,
                    cause = e
                )
            )
        }
    }

    /**
     * Validate username input.
     *
     * @param username The username to validate
     * @return PasswordResetResult.Error if validation fails, null if valid
     */
    private fun validateUsername(username: String): PasswordResetResult.Error? {
        return when {
            username.isBlank() -> PasswordResetResult.Error(
                message = "Username cannot be empty",
                errorType = PasswordResetErrorType.INVALID_INPUT
            )
            username.length < MIN_USERNAME_LENGTH -> PasswordResetResult.Error(
                message = "Username must be at least $MIN_USERNAME_LENGTH characters",
                errorType = PasswordResetErrorType.INVALID_INPUT
            )
            else -> null
        }
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
    }
}
