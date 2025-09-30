package net.hitpromo.hitpromoworkstation.presentation.passwordchange

import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.usecase.PasswordStrength

/**
 * UI State for the password change screen using MVI pattern.
 */
data class PasswordChangeUiState(
    val username: String = "",
    val sessionId: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.WEAK,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordChangeSuccessful: Boolean = false,
    val authenticatedUser: User? = null,
    val successTimestamp: Long? = null,
    val meetsMinimumRequirements: Boolean = false
) {
    /**
     * Check if there's an active error.
     */
    val hasError: Boolean
        get() = errorMessage != null

    /**
     * Check if the form should be enabled.
     */
    val isFormEnabled: Boolean
        get() = !isLoading

    /**
     * Check if passwords match.
     */
    val passwordsMatch: Boolean
        get() = newPassword.isNotEmpty() &&
                confirmPassword.isNotEmpty() &&
                newPassword == confirmPassword

    /**
     * Check if submit button should be enabled.
     * Relies on meetsMinimumRequirements field set by ViewModel from UseCase validation.
     */
    val isSubmitEnabled: Boolean
        get() = isFormEnabled &&
                newPassword.isNotEmpty() &&
                confirmPassword.isNotEmpty() &&
                passwordsMatch &&
                meetsMinimumRequirements

    /**
     * Get display message for the user.
     */
    val displayMessage: String?
        get() = errorMessage

    companion object {
        /**
         * Initial state when the screen loads.
         */
        fun Initial(username: String, sessionId: String) = PasswordChangeUiState(
            username = username,
            sessionId = sessionId
        )

        /**
         * Loading state during password change operation.
         */
        fun Loading(currentState: PasswordChangeUiState) = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        /**
         * Success state after password change.
         */
        fun Success(user: User, currentState: PasswordChangeUiState) = currentState.copy(
            isLoading = false,
            isPasswordChangeSuccessful = true,
            authenticatedUser = user,
            errorMessage = null,
            successTimestamp = System.currentTimeMillis()
        )

        /**
         * Error state when password change fails.
         */
        fun Error(message: String, currentState: PasswordChangeUiState) = currentState.copy(
            isLoading = false,
            errorMessage = message
        )
    }
}