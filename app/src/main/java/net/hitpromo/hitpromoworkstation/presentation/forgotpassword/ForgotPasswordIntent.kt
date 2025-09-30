package net.hitpromo.hitpromoworkstation.presentation.forgotpassword

/**
 * MVI Intent sealed class representing all possible user actions on the forgot password screen.
 *
 * Each intent corresponds to a user interaction that triggers state changes in the ViewModel.
 */
sealed class ForgotPasswordIntent {
    /**
     * User requests a password reset by submitting their username.
     *
     * @param username The username/email for which to request a password reset
     */
    data class RequestPasswordReset(val username: String) : ForgotPasswordIntent()

    /**
     * User confirms the password reset by submitting the verification code and new password.
     *
     * @param verificationCode The code received via email/SMS
     * @param newPassword The desired new password
     * @param confirmPassword The confirmation of the new password
     */
    data class ConfirmPasswordReset(
        val verificationCode: String,
        val newPassword: String,
        val confirmPassword: String
    ) : ForgotPasswordIntent()

    /**
     * User wants to navigate back to the previous step or screen.
     */
    object NavigateBack : ForgotPasswordIntent()

    /**
     * User wants to resend the verification code.
     */
    object ResendCode : ForgotPasswordIntent()

    /**
     * Clear any error messages from the UI.
     */
    object ClearError : ForgotPasswordIntent()

    /**
     * User wants to return to the login screen after successful password reset.
     */
    object ReturnToLogin : ForgotPasswordIntent()
}
