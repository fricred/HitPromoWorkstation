package net.hitpromo.hitpromoworkstation.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.hitpromo.hitpromoworkstation.data.local.UserPreferences
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.usecase.SignInUseCase
import net.hitpromo.hitpromoworkstation.domain.usecase.SignOutUseCase
import javax.inject.Inject

/**
 * ViewModel for the login screen implementing MVVM + MVI architecture.
 *
 * Handles authentication state management and user interactions using
 * reactive streams and use cases.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Initialize UI state based on stored preferences
        initializeState()
    }

    /**
     * Handle user intents from the UI.
     */
    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SignIn -> {
                signIn(intent.username, intent.password)
            }
            is LoginIntent.SignOut -> {
                signOut()
            }
            is LoginIntent.ToggleRememberMe -> {
                toggleRememberMe(intent.remember)
            }
            is LoginIntent.ForgotPassword -> {
                handleForgotPassword()
            }
            is LoginIntent.ClearError -> {
                clearError()
            }
            is LoginIntent.RefreshSession -> {
                refreshSession()
            }
        }
    }

    /**
     * Sign in with username and password.
     */
    private fun signIn(username: String, password: String) {
        viewModelScope.launch {
            signInUseCase(username, password).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _uiState.value = LoginUiState.Loading(_uiState.value)
                    }
                    is AuthResult.Success -> {
                        val currentRememberMe = _uiState.value.rememberMe
                        _uiState.value = LoginUiState.Success(
                            user = result.data,
                            rememberMe = currentRememberMe
                        )

                        // Save remember me preference
                        if (currentRememberMe) {
                            userPreferences.setRememberMe(true)
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.value = LoginUiState.Error(
                            message = result.message,
                            currentState = _uiState.value
                        )
                    }
                }
            }
        }
    }

    /**
     * Sign out the current user.
     */
    private fun signOut() {
        viewModelScope.launch {
            signOutUseCase().collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _uiState.value = LoginUiState.Loading(_uiState.value)
                    }
                    is AuthResult.Success -> {
                        val rememberMe = userPreferences.rememberMe.first()
                        _uiState.value = LoginUiState.Unauthenticated(rememberMe)
                    }
                    is AuthResult.Error -> {
                        _uiState.value = LoginUiState.Error(
                            message = "Failed to sign out: ${result.message}",
                            currentState = _uiState.value
                        )
                    }
                }
            }
        }
    }

    /**
     * Toggle remember me preference.
     */
    private fun toggleRememberMe(remember: Boolean) {
        viewModelScope.launch {
            userPreferences.setRememberMe(remember)
            _uiState.value = _uiState.value.copy(rememberMe = remember)
        }
    }

    /**
     * Handle forgot password action.
     */
    private fun handleForgotPassword() {
        // For now, show a message to contact administrator
        _uiState.value = _uiState.value.copy(
            errorMessage = "Please contact your system administrator for password reset assistance."
        )
    }

    /**
     * Clear any error messages.
     */
    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Refresh the authentication session.
     */
    private fun refreshSession() {
        viewModelScope.launch {
            // This would typically call a refresh session use case
            // For now, we'll validate the current session
            val isLoggedIn = userPreferences.isLoggedIn.first()
            val userId = userPreferences.userId.first()

            if (isLoggedIn && userId != null) {
                // Session appears valid, keep current state
                _uiState.value = _uiState.value.copy(isSessionValidated = true)
            } else {
                // Session invalid, redirect to login
                val rememberMe = userPreferences.rememberMe.first()
                _uiState.value = LoginUiState.Unauthenticated(rememberMe)
            }
        }
    }

    /**
     * Initialize the UI state based on stored preferences.
     */
    private fun initializeState() {
        viewModelScope.launch {
            try {
                val isLoggedIn = userPreferences.isLoggedIn.first()
                val rememberMe = userPreferences.rememberMe.first()

                if (isLoggedIn) {
                    // Check if we have valid session data
                    val userId = userPreferences.userId.first()
                    val username = userPreferences.username.first()
                    val email = userPreferences.userEmail.first()

                    if (userId != null && username != null && email != null) {
                        // We could restore the user here, but for security,
                        // we'll require re-authentication
                        _uiState.value = LoginUiState.Unauthenticated(rememberMe)
                    } else {
                        _uiState.value = LoginUiState.Unauthenticated(rememberMe)
                    }
                } else {
                    _uiState.value = LoginUiState.Unauthenticated(rememberMe)
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    message = "Failed to initialize login state",
                    currentState = LoginUiState.Initial
                )
            }
        }
    }
}