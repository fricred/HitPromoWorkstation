package net.hitpromo.hitpromoworkstation.presentation.forgotpassword

/**
 * Enum representing the different steps in the forgot password flow.
 *
 * The password reset process follows a multi-step workflow:
 * 1. User enters their username to request a password reset
 * 2. User receives a verification code and enters it along with their new password
 * 3. User sees a success message and can return to login
 */
enum class ForgotPasswordStep {
    /**
     * Initial step where user enters their username to request a password reset code.
     */
    ENTER_USERNAME,

    /**
     * Second step where user enters the verification code received
     * and their desired new password.
     */
    VERIFY_CODE,

    /**
     * Final step showing success message after password has been reset.
     */
    SUCCESS
}
