package net.hitpromo.hitpromoworkstation.presentation.forgotpassword

/**
 * UI State for the forgot password screen using MVI pattern.
 *
 * Represents the complete state of the password reset flow including
 * current step, user input, loading state, and messages.
 */
data class ForgotPasswordUiState(
    /**
     * The current step in the password reset flow.
     */
    val currentStep: ForgotPasswordStep = ForgotPasswordStep.ENTER_USERNAME,

    /**
     * The username entered by the user for password reset.
     */
    val username: String = "",

    /**
     * The masked destination where the code was sent (e.g., "a***@example.com").
     */
    val deliveryDestination: String? = null,

    /**
     * Indicates if an operation is currently in progress.
     */
    val isLoading: Boolean = false,

    /**
     * Error message to display to the user, if any.
     */
    val errorMessage: String? = null,

    /**
     * Success message to display to the user, if any.
     */
    val successMessage: String? = null
) {
    /**
     * Check if there's an active error.
     */
    val hasError: Boolean
        get() = errorMessage != null

    /**
     * Check if the form should be enabled for user input.
     * Disabled during loading operations.
     */
    val isFormEnabled: Boolean
        get() = !isLoading

    companion object {
        /**
         * Initial state when the screen loads.
         * Shows the username entry step with no data.
         */
        val Initial = ForgotPasswordUiState()

        /**
         * Loading state during an async operation.
         * Preserves the current state but sets loading flag.
         *
         * @param currentState The current state to preserve
         */
        fun Loading(currentState: ForgotPasswordUiState) = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        /**
         * State after verification code has been sent successfully.
         * Transitions to the code verification step.
         *
         * @param username The username for which the code was sent
         * @param deliveryDestination The masked destination (e.g., "a***@example.com")
         */
        fun CodeSent(
            username: String,
            deliveryDestination: String?
        ) = ForgotPasswordUiState(
            currentStep = ForgotPasswordStep.VERIFY_CODE,
            username = username,
            deliveryDestination = deliveryDestination,
            isLoading = false,
            errorMessage = null,
            successMessage = "Verification code sent to $deliveryDestination"
        )

        /**
         * State after password has been reset successfully.
         * Transitions to the success step.
         */
        fun ResetComplete() = ForgotPasswordUiState(
            currentStep = ForgotPasswordStep.SUCCESS,
            isLoading = false,
            errorMessage = null,
            successMessage = "Password has been reset successfully. You can now log in with your new password."
        )

        /**
         * Error state when an operation fails.
         * Preserves the current state but adds an error message.
         *
         * @param message The error message to display
         * @param currentState The current state to preserve
         */
        fun Error(message: String, currentState: ForgotPasswordUiState) = currentState.copy(
            isLoading = false,
            errorMessage = message,
            successMessage = null
        )
    }
}
