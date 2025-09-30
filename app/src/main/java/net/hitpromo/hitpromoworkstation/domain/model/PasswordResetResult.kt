package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Represents the result of password reset operations.
 *
 * This sealed class encapsulates all possible states during the password reset flow,
 * from requesting a reset code to confirming the new password.
 */
sealed class PasswordResetResult {

    /**
     * Password reset code has been successfully sent to the user's registered contact method.
     *
     * @param deliveryDestination The masked destination where the code was sent (e.g., "s***@example.com")
     */
    data class CodeSent(val deliveryDestination: String) : PasswordResetResult()

    /**
     * Password reset has been completed successfully.
     */
    object ResetComplete : PasswordResetResult()

    /**
     * Password reset operation is in progress.
     */
    object Loading : PasswordResetResult()

    /**
     * Password reset operation failed.
     *
     * @param message Human-readable error message
     * @param errorType The specific type of error that occurred
     * @param cause Optional underlying exception
     */
    data class Error(
        val message: String,
        val errorType: PasswordResetErrorType,
        val cause: Throwable? = null
    ) : PasswordResetResult()
}

/**
 * Types of errors that can occur during password reset operations.
 */
enum class PasswordResetErrorType {
    /**
     * The provided username does not exist in the system.
     */
    USER_NOT_FOUND,

    /**
     * The verification code provided is invalid or incorrect.
     */
    INVALID_CODE,

    /**
     * The verification code has expired.
     */
    CODE_EXPIRED,

    /**
     * Too many reset attempts have been made.
     */
    LIMIT_EXCEEDED,

    /**
     * The new password does not meet complexity requirements.
     */
    INVALID_PASSWORD,

    /**
     * Network connectivity issue.
     */
    NETWORK_ERROR,

    /**
     * An unexpected error occurred.
     */
    UNKNOWN
}
