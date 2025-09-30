package net.hitpromo.hitpromoworkstation.presentation.passwordchange

/**
 * MVI Intent sealed class representing all possible user actions on the password change screen.
 */
sealed class PasswordChangeIntent {
    /**
     * User changed the new password field.
     */
    data class NewPasswordChanged(val password: String) : PasswordChangeIntent()

    /**
     * User changed the confirm password field.
     */
    data class ConfirmPasswordChanged(val password: String) : PasswordChangeIntent()

    /**
     * User toggled password visibility for a specific field.
     */
    data class TogglePasswordVisibility(val field: PasswordField) : PasswordChangeIntent()

    /**
     * User submitted the password change form.
     */
    object SubmitPasswordChange : PasswordChangeIntent()

    /**
     * User cancelled the password change flow.
     */
    object Cancel : PasswordChangeIntent()

    /**
     * Clear any error messages.
     */
    object ClearError : PasswordChangeIntent()
}

/**
 * Enum representing password fields that can have visibility toggled.
 */
enum class PasswordField {
    NEW_PASSWORD,
    CONFIRM_PASSWORD
}