package net.hitpromo.hitpromoworkstation.presentation.passwordchange

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import net.hitpromo.hitpromoworkstation.BuildConfig
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import net.hitpromo.hitpromoworkstation.domain.usecase.ConfirmNewPasswordUseCase
import javax.inject.Inject

/**
 * ViewModel for the password change screen implementing MVVM + MVI architecture.
 *
 * Handles password change state management, validation, and user interactions
 * using reactive streams and use cases.
 */
@HiltViewModel
class PasswordChangeViewModel @Inject constructor(
    private val confirmNewPasswordUseCase: ConfirmNewPasswordUseCase,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var username: String = savedStateHandle.get<String>("username") ?: ""
    private var sessionId: String = savedStateHandle.get<String>("sessionId") ?: ""
    private var isInitialized = false

    private val _uiState = MutableStateFlow(
        PasswordChangeUiState.Initial("", "")
    )
    val uiState: StateFlow<PasswordChangeUiState> = _uiState.asStateFlow()

    init {
        // Check if we got values from SavedStateHandle (when using navigation)
        if (username.isNotEmpty() && sessionId.isNotEmpty()) {
            initializeState(username, sessionId)
        }
    }

    /**
     * Initialize the ViewModel with username and sessionId.
     * This should be called when the screen is composed if not using navigation.
     *
     * @param username The username requiring password change
     * @param sessionId The session ID from NEW_PASSWORD_REQUIRED challenge
     */
    fun initialize(username: String, sessionId: String) {
        if (!isInitialized) {
            this.username = username
            this.sessionId = sessionId
            initializeState(username, sessionId)
        } else {
            // Warn if attempting to re-initialize with different values
            if (this.username != username || this.sessionId != sessionId) {
                Log.w(TAG, "Attempting to re-initialize ViewModel with different values. " +
                        "Current: username=$this.username, sessionId=$this.sessionId; " +
                        "New: username=$username, sessionId=$sessionId")
            }
        }
    }

    private fun initializeState(username: String, sessionId: String) {
        if (username.isEmpty() || sessionId.isEmpty()) {
            _uiState.value = PasswordChangeUiState(
                username = "",
                sessionId = "",
                newPassword = "",
                confirmPassword = "",
                passwordStrength = net.hitpromo.hitpromoworkstation.domain.usecase.PasswordStrength.WEAK,
                isNewPasswordVisible = false,
                isConfirmPasswordVisible = false,
                isLoading = false,
                errorMessage = "Invalid password change session. Please sign in again.",
                isPasswordChangeSuccessful = false
            )
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "PasswordChangeViewModel initialized with invalid session data")
            }
        } else {
            _uiState.value = PasswordChangeUiState.Initial(username, sessionId)
            isInitialized = true
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "PasswordChangeViewModel initialized for user: $username with sessionId: $sessionId")
            } else {
                Log.d(TAG, "PasswordChangeViewModel initialized")
            }
        }
    }

    /**
     * Handle user intents from the UI.
     */
    fun handleIntent(intent: PasswordChangeIntent) {
        when (intent) {
            is PasswordChangeIntent.NewPasswordChanged -> {
                updateNewPassword(intent.password)
            }
            is PasswordChangeIntent.ConfirmPasswordChanged -> {
                updateConfirmPassword(intent.password)
            }
            is PasswordChangeIntent.TogglePasswordVisibility -> {
                togglePasswordVisibility(intent.field)
            }
            is PasswordChangeIntent.SubmitPasswordChange -> {
                submitPasswordChange()
            }
            is PasswordChangeIntent.Cancel -> {
                cancelPasswordChange()
            }
            is PasswordChangeIntent.ClearError -> {
                clearError()
            }
        }
    }

    /**
     * Update the new password field and recalculate strength and validation.
     */
    private fun updateNewPassword(password: String) {
        val strength = confirmNewPasswordUseCase.calculatePasswordStrength(password)
        val meetsRequirements = confirmNewPasswordUseCase.meetsMinimumRequirements(password)
        _uiState.value = _uiState.value.copy(
            newPassword = password,
            passwordStrength = strength,
            meetsMinimumRequirements = meetsRequirements,
            errorMessage = null
        )
    }

    /**
     * Update the confirm password field.
     */
    private fun updateConfirmPassword(password: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = password,
            errorMessage = null
        )
    }

    /**
     * Toggle password visibility for a specific field.
     */
    private fun togglePasswordVisibility(field: PasswordField) {
        when (field) {
            PasswordField.NEW_PASSWORD -> {
                _uiState.value = _uiState.value.copy(
                    isNewPasswordVisible = !_uiState.value.isNewPasswordVisible
                )
            }
            PasswordField.CONFIRM_PASSWORD -> {
                _uiState.value = _uiState.value.copy(
                    isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
                )
            }
        }
    }

    /**
     * Submit the password change request.
     */
    private fun submitPasswordChange() {
        val currentState = _uiState.value

        // Validate passwords match
        if (currentState.newPassword != currentState.confirmPassword) {
            _uiState.value = PasswordChangeUiState.Error(
                "Passwords do not match",
                currentState
            )
            return
        }

        // Validate minimum requirements
        if (!confirmNewPasswordUseCase.meetsMinimumRequirements(currentState.newPassword)) {
            _uiState.value = PasswordChangeUiState.Error(
                "Password does not meet minimum requirements",
                currentState
            )
            return
        }

        viewModelScope.launch {
            confirmNewPasswordUseCase(
                sessionId = currentState.sessionId,
                newPassword = currentState.newPassword,
                confirmPassword = currentState.confirmPassword
            )
                .catch { exception ->
                    Log.e(TAG, "Password change flow error", exception)
                    emit(AuthResult.Error(
                        "Password change failed: ${exception.message ?: "Unknown error"}",
                        exception
                    ))
                }
                .collect { result ->
                    when (result) {
                        is AuthResult.Loading -> {
                            _uiState.value = PasswordChangeUiState.Loading(_uiState.value)
                        }
                        is AuthResult.Success -> {
                            _uiState.value = PasswordChangeUiState.Success(
                                user = result.data,
                                currentState = _uiState.value
                            )
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "Password change successful for user: $username")
                            } else {
                                Log.d(TAG, "Password change successful")
                            }
                        }
                        is AuthResult.Error -> {
                            _uiState.value = PasswordChangeUiState.Error(
                                message = result.message,
                                currentState = _uiState.value
                            )
                        }
                        is AuthResult.NewPasswordRequired -> {
                            // This should not happen during password change
                            _uiState.value = PasswordChangeUiState.Error(
                                message = "Unexpected error: Password change required again",
                                currentState = _uiState.value
                            )
                        }
                    }
                }
        }
    }

    /**
     * Cancel the password change flow.
     */
    private fun cancelPasswordChange() {
        // Cancel the session
        authRepository.cancelPasswordChangeSession(sessionId)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Password change cancelled for user: $username")
        } else {
            Log.d(TAG, "Password change cancelled")
        }
        // The UI should navigate back to login screen
    }

    /**
     * Clear any error messages.
     */
    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        // Always clean up session to prevent memory leaks
        // The repository will handle if session is already cleaned up
        authRepository.cancelPasswordChangeSession(sessionId)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "ViewModel cleared, cleaning up session for user: $username")
        } else {
            Log.d(TAG, "ViewModel cleared, cleaning up session")
        }
    }

    companion object {
        private const val TAG = "PasswordChangeViewModel"
    }
}