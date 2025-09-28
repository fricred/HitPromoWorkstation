package net.hitpromo.hitpromoworkstation.presentation.login

import net.hitpromo.hitpromoworkstation.domain.model.User

/**
 * UI State for the login screen using MVI pattern.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null,
    val rememberMe: Boolean = false,
    val isSessionValidated: Boolean = false
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
     * Get display message for the user.
     */
    val displayMessage: String?
        get() = errorMessage

    companion object {
        /**
         * Initial state when the screen loads.
         */
        val Initial = LoginUiState()

        /**
         * Loading state during authentication.
         */
        fun Loading(currentState: LoginUiState) = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        /**
         * Success state after authentication.
         */
        fun Success(user: User, rememberMe: Boolean = false) = LoginUiState(
            isLoading = false,
            isAuthenticated = true,
            user = user,
            errorMessage = null,
            rememberMe = rememberMe,
            isSessionValidated = true
        )

        /**
         * Error state when authentication fails.
         */
        fun Error(message: String, currentState: LoginUiState) = currentState.copy(
            isLoading = false,
            isAuthenticated = false,
            errorMessage = message
        )

        /**
         * Unauthenticated state.
         */
        fun Unauthenticated(rememberMe: Boolean = false) = LoginUiState(
            isLoading = false,
            isAuthenticated = false,
            user = null,
            errorMessage = null,
            rememberMe = rememberMe,
            isSessionValidated = false
        )
    }
}