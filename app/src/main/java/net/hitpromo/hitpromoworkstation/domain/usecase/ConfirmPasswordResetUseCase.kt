package net.hitpromo.hitpromoworkstation.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetErrorType
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetResult
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for confirming a password reset with verification code and new password.
 *
 * This use case handles the business logic for completing the password reset flow,
 * including input validation, password complexity requirements, and coordinating
 * with the repository to set the new password.
 */
class ConfirmPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val confirmNewPasswordUseCase: ConfirmNewPasswordUseCase
) {

    /**
     * Execute the password reset confirmation operation.
     *
     * @param username The username for which to reset the password
     * @param verificationCode The verification code received via email/SMS
     * @param newPassword The new password to set
     * @param confirmPassword The confirmation password (must match newPassword)
     * @return Flow of PasswordResetResult indicating the progress and result of the operation
     */
    operator fun invoke(
        username: String,
        verificationCode: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<PasswordResetResult> = flow {
        try {
            emit(PasswordResetResult.Loading)

            // Validate passwords match
            if (newPassword != confirmPassword) {
                emit(
                    PasswordResetResult.Error(
                        message = "Passwords do not match",
                        errorType = PasswordResetErrorType.INVALID_PASSWORD
                    )
                )
                return@flow
            }

            // Validate username
            val usernameValidation = validateUsername(username)
            if (usernameValidation != null) {
                emit(usernameValidation)
                return@flow
            }

            // Validate verification code
            val codeValidation = validateVerificationCode(verificationCode)
            if (codeValidation != null) {
                emit(codeValidation)
                return@flow
            }

            // Validate password using existing password validation logic
            val passwordValidation = confirmNewPasswordUseCase.validatePassword(newPassword)
            if (passwordValidation != null) {
                emit(
                    PasswordResetResult.Error(
                        message = passwordValidation,
                        errorType = PasswordResetErrorType.INVALID_PASSWORD
                    )
                )
                return@flow
            }

            // Perform password reset
            val result = authRepository.confirmPasswordReset(
                username = username.trim(),
                verificationCode = verificationCode.trim(),
                newPassword = newPassword
            )
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
                errorType = PasswordResetErrorType.INVALID_PASSWORD
            )
            username.length < MIN_USERNAME_LENGTH -> PasswordResetResult.Error(
                message = "Username must be at least $MIN_USERNAME_LENGTH characters",
                errorType = PasswordResetErrorType.INVALID_PASSWORD
            )
            else -> null
        }
    }

    /**
     * Validate verification code input.
     *
     * @param verificationCode The verification code to validate
     * @return PasswordResetResult.Error if validation fails, null if valid
     */
    private fun validateVerificationCode(verificationCode: String): PasswordResetResult.Error? {
        return when {
            verificationCode.isBlank() -> PasswordResetResult.Error(
                message = "Verification code cannot be empty",
                errorType = PasswordResetErrorType.INVALID_CODE
            )
            verificationCode.length != VERIFICATION_CODE_LENGTH -> PasswordResetResult.Error(
                message = "Verification code must be $VERIFICATION_CODE_LENGTH digits",
                errorType = PasswordResetErrorType.INVALID_CODE
            )
            !verificationCode.all { it.isDigit() } -> PasswordResetResult.Error(
                message = "Verification code must contain only digits",
                errorType = PasswordResetErrorType.INVALID_CODE
            )
            else -> null
        }
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val VERIFICATION_CODE_LENGTH = 6
    }
}
