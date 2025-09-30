package net.hitpromo.hitpromoworkstation.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for confirming a new password after receiving NEW_PASSWORD_REQUIRED challenge.
 *
 * Handles password validation, strength calculation, and authentication with AWS Cognito.
 */
class ConfirmNewPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Confirm new password with validation and authentication.
     *
     * @param sessionId The session ID from the NewPasswordRequired result
     * @param newPassword The new password to set
     * @param confirmPassword The confirmation password (must match newPassword)
     * @return Flow of AuthResult indicating progress and result
     */
    operator fun invoke(
        sessionId: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<AuthResult<User>> = flow {
        emit(AuthResult.Loading)

        // Validate passwords match
        if (newPassword != confirmPassword) {
            emit(AuthResult.Error("Passwords do not match"))
            return@flow
        }

        // Validate password meets requirements
        val validationResult = validatePassword(newPassword)
        if (validationResult != null) {
            emit(AuthResult.Error(validationResult))
            return@flow
        }

        // Perform password change
        val result = authRepository.confirmNewPassword(sessionId, newPassword)
        emit(result)
    }

    /**
     * Validate password meets AWS Cognito requirements.
     *
     * @param password The password to validate
     * @return Error message if validation fails, null if valid
     */
    fun validatePassword(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters long"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !hasSpecialChar(password) -> "Password must contain at least one special character"
            else -> null
        }
    }

    /**
     * Check if password has at least one special character.
     *
     * @param password The password to check
     * @return True if password contains at least one special character, false otherwise
     */
    private fun hasSpecialChar(password: String): Boolean {
        return password.any { !it.isLetterOrDigit() }
    }

    /**
     * Check if password meets minimum requirements.
     *
     * @param password The password to check
     * @return True if password is valid, false otherwise
     */
    fun meetsMinimumRequirements(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                hasSpecialChar(password)
    }

    /**
     * Calculate password strength.
     *
     * @param password The password to evaluate
     * @return PasswordStrength enum value
     */
    fun calculatePasswordStrength(password: String): PasswordStrength {
        var score = 0

        // Length scoring
        score += when {
            password.length >= 12 -> 2
            password.length >= 8 -> 1
            else -> 0
        }

        // Character variety scoring
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        // Additional complexity bonus
        if (password.length >= 10 && score >= 4) score++

        return when {
            score <= 2 -> PasswordStrength.WEAK
            score == 3 -> PasswordStrength.MEDIUM
            score == 4 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }

    /**
     * Check individual password requirements.
     *
     * @param password The password to check
     * @return Map of requirement name to whether it's met
     */
    fun checkRequirements(password: String): PasswordRequirements {
        return PasswordRequirements(
            hasMinLength = password.length >= 8,
            hasUppercase = password.any { it.isUpperCase() },
            hasLowercase = password.any { it.isLowerCase() },
            hasDigit = password.any { it.isDigit() },
            hasSpecialChar = password.any { !it.isLetterOrDigit() }
        )
    }
}

/**
 * Password strength levels.
 */
enum class PasswordStrength {
    WEAK,
    MEDIUM,
    STRONG,
    VERY_STRONG
}

/**
 * Password requirements checklist.
 */
data class PasswordRequirements(
    val hasMinLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasLowercase: Boolean = false,
    val hasDigit: Boolean = false,
    val hasSpecialChar: Boolean = false
) {
    /**
     * Check if all required (non-optional) requirements are met.
     */
    val allRequiredMet: Boolean
        get() = hasMinLength && hasUppercase && hasLowercase && hasDigit && hasSpecialChar
}