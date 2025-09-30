package net.hitpromo.hitpromoworkstation.presentation.forgotpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetErrorType
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetResult
import net.hitpromo.hitpromoworkstation.domain.usecase.ConfirmPasswordResetUseCase
import net.hitpromo.hitpromoworkstation.domain.usecase.RequestPasswordResetUseCase
import javax.inject.Inject

/**
 * ViewModel for the forgot password screen implementing MVVM + MVI architecture.
 *
 * Manages the multi-step password reset flow including:
 * 1. Requesting a password reset code
 * 2. Verifying the code and setting a new password
 * 3. Handling errors and user navigation
 *
 * Uses reactive state management with StateFlow to communicate state changes to the UI.
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase,
    private val confirmPasswordResetUseCase: ConfirmPasswordResetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState.Initial)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    /**
     * Handle user intents from the UI.
     *
     * @param intent The user action to process
     */
    fun handleIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.RequestPasswordReset -> {
                requestPasswordReset(intent.username)
            }
            is ForgotPasswordIntent.ConfirmPasswordReset -> {
                confirmPasswordReset(
                    code = intent.verificationCode,
                    password = intent.newPassword,
                    confirmPassword = intent.confirmPassword
                )
            }
            is ForgotPasswordIntent.NavigateBack -> {
                navigateBack()
            }
            is ForgotPasswordIntent.ResendCode -> {
                resendCode()
            }
            is ForgotPasswordIntent.ClearError -> {
                clearError()
            }
            is ForgotPasswordIntent.ReturnToLogin -> {
                resetState()
            }
        }
    }

    /**
     * Request a password reset for the given username.
     *
     * Validates the username and initiates the password reset flow by
     * requesting a verification code from AWS Cognito.
     *
     * @param username The username/email for password reset
     */
    private fun requestPasswordReset(username: String) {
        viewModelScope.launch {
            // Validate username
            if (username.isBlank()) {
                _uiState.value = ForgotPasswordUiState.Error(
                    message = "Username cannot be empty",
                    currentState = _uiState.value
                )
                return@launch
            }

            requestPasswordResetUseCase(username)
                .catch { exception ->
                    Log.e(TAG, "Password reset request flow error", exception)
                    emit(PasswordResetResult.Error(
                        message = "Failed to request password reset: ${exception.message ?: "Unknown error"}",
                        errorType = PasswordResetErrorType.UNKNOWN,
                        cause = exception
                    ))
                }
                .collect { result ->
                    when (result) {
                        is PasswordResetResult.Loading -> {
                            _uiState.value = ForgotPasswordUiState.Loading(_uiState.value)
                        }
                        is PasswordResetResult.CodeSent -> {
                            _uiState.value = ForgotPasswordUiState.CodeSent(
                                username = username,
                                deliveryDestination = result.deliveryDestination
                            )
                        }
                        is PasswordResetResult.ResetComplete -> {
                            // This should not happen at this stage
                            Log.w(TAG, "Unexpected ResetComplete state during code request")
                        }
                        is PasswordResetResult.Error -> {
                            _uiState.value = ForgotPasswordUiState.Error(
                                message = result.message,
                                currentState = _uiState.value
                            )
                        }
                    }
                }
        }
    }

    /**
     * Confirm the password reset with the verification code and new password.
     *
     * Validates the inputs and completes the password reset process.
     *
     * @param code The verification code received
     * @param password The new password
     * @param confirmPassword The password confirmation
     */
    private fun confirmPasswordReset(code: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Validate inputs
            if (code.isBlank()) {
                _uiState.value = ForgotPasswordUiState.Error(
                    message = "Verification code cannot be empty",
                    currentState = _uiState.value
                )
                return@launch
            }

            if (password.isBlank()) {
                _uiState.value = ForgotPasswordUiState.Error(
                    message = "Password cannot be empty",
                    currentState = _uiState.value
                )
                return@launch
            }

            if (password != confirmPassword) {
                _uiState.value = ForgotPasswordUiState.Error(
                    message = "Passwords do not match",
                    currentState = _uiState.value
                )
                return@launch
            }

            if (password.length < 8) {
                _uiState.value = ForgotPasswordUiState.Error(
                    message = "Password must be at least 8 characters",
                    currentState = _uiState.value
                )
                return@launch
            }

            val username = _uiState.value.username
            if (username.isBlank()) {
                _uiState.value = ForgotPasswordUiState.Error(
                    message = "Username not found. Please restart the process.",
                    currentState = _uiState.value
                )
                return@launch
            }

            confirmPasswordResetUseCase(username, code, password, confirmPassword)
                .catch { exception ->
                    Log.e(TAG, "Password reset confirmation flow error", exception)
                    emit(PasswordResetResult.Error(
                        message = "Failed to confirm password reset: ${exception.message ?: "Unknown error"}",
                        errorType = PasswordResetErrorType.UNKNOWN,
                        cause = exception
                    ))
                }
                .collect { result ->
                    when (result) {
                        is PasswordResetResult.Loading -> {
                            _uiState.value = ForgotPasswordUiState.Loading(_uiState.value)
                        }
                        is PasswordResetResult.CodeSent -> {
                            // This should not happen at this stage
                            Log.w(TAG, "Unexpected CodeSent state during confirmation")
                        }
                        is PasswordResetResult.ResetComplete -> {
                            _uiState.value = ForgotPasswordUiState.ResetComplete()
                        }
                        is PasswordResetResult.Error -> {
                            _uiState.value = ForgotPasswordUiState.Error(
                                message = result.message,
                                currentState = _uiState.value
                            )
                        }
                    }
                }
        }
    }

    /**
     * Navigate back to the previous step or initial state.
     *
     * From VERIFY_CODE step, returns to ENTER_USERNAME.
     * From other steps, returns to initial state.
     */
    private fun navigateBack() {
        when (_uiState.value.currentStep) {
            ForgotPasswordStep.VERIFY_CODE -> {
                // Go back to username entry, but clear any messages
                _uiState.value = _uiState.value.copy(
                    currentStep = ForgotPasswordStep.ENTER_USERNAME,
                    errorMessage = null,
                    successMessage = null
                )
            }
            ForgotPasswordStep.SUCCESS -> {
                // Reset to initial state
                resetState()
            }
            else -> {
                // Already at the first step or unknown state, reset
                resetState()
            }
        }
    }

    /**
     * Resend the verification code to the user.
     *
     * Re-initiates the password reset request using the stored username.
     */
    private fun resendCode() {
        val username = _uiState.value.username
        if (username.isBlank()) {
            _uiState.value = ForgotPasswordUiState.Error(
                message = "Username not found. Please restart the process.",
                currentState = _uiState.value
            )
            return
        }

        Log.d(TAG, "Resending verification code for username: $username")
        requestPasswordReset(username)
    }

    /**
     * Clear any error messages from the current state.
     */
    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Reset the state to initial, typically when returning to login.
     */
    private fun resetState() {
        _uiState.value = ForgotPasswordUiState.Initial
    }

    companion object {
        private const val TAG = "ForgotPasswordViewModel"
    }
}
