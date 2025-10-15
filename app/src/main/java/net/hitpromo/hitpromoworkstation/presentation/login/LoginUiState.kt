package net.hitpromo.hitpromoworkstation.presentation.login

import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.scanner.LogLevel

/**
 * UI State for the login screen using MVI pattern.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null,
    val rememberMe: Boolean = false,
    val isSessionValidated: Boolean = false,
    val requirePasswordChange: Boolean = false,
    val passwordChangeUsername: String? = null,
    val passwordChangeSessionId: String? = null,
    val isReadyToScan: Boolean = false,
    val scannedBadgeId: String? = null,

    // Scanner state
    val scannerStatus: ScannerStatus = ScannerStatus.Initializing,
    val scannerName: String? = null,
    val scannerId: Int? = null,
    val debugLogs: List<DebugLog> = emptyList(),
    val showDebugModal: Boolean = false,
    val maxDebugLogs: Int = 100
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

/**
 * Scanner status states for UI display.
 */
enum class ScannerStatus {
    Initializing,       // SDK is initializing
    ScannerNotFound,    // No scanner detected after timeout
    ScannerFound,       // Scanner detected but not connected
    Connecting,         // Attempting to connect
    Connected,          // Connected but not actively scanning
    ReadyToScan,        // Ready to scan barcodes
    Scanning,           // Actively scanning
    Disconnected,       // Scanner disconnected
    Error               // Error state
}

/**
 * Debug log entry for scanner events.
 */
data class DebugLog(
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel,
    val message: String
)