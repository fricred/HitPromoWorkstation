package net.hitpromo.hitpromoworkstation.presentation.login

import net.hitpromo.hitpromoworkstation.domain.scanner.LogLevel

/**
 * MVI Intent sealed class representing all possible user actions on the login screen.
 */
sealed class LoginIntent {
    /**
     * User wants to sign in with credentials (legacy - kept for compatibility).
     */
    data class SignIn(val username: String, val password: String) : LoginIntent()

    /**
     * User scanned their badge to sign in.
     */
    data class ScanBadge(val badgeId: String) : LoginIntent()

    /**
     * User clicked the "Scan ID to Login" button - ready to capture scanner input.
     */
    data object ReadyToScan : LoginIntent()

    /**
     * User wants to sign out.
     */
    data object SignOut : LoginIntent()

    /**
     * User wants to toggle remember me preference.
     */
    data class ToggleRememberMe(val remember: Boolean) : LoginIntent()

    /**
     * User clicked forgot password (deprecated for badge scanning).
     */
    data object ForgotPassword : LoginIntent()

    /**
     * Clear any error messages.
     */
    data object ClearError : LoginIntent()

    /**
     * Refresh the authentication session.
     */
    data object RefreshSession : LoginIntent()

    /**
     * Clear password change state after successful password change.
     */
    data object ClearPasswordChangeState : LoginIntent()

    // ========================================
    // Form input intents (two-step authentication)
    // ========================================

    /**
     * User updated the badge ID input field.
     */
    data class UpdateBadgeId(val badgeId: String) : LoginIntent()

    /**
     * User updated the machine ID input field.
     */
    data class UpdateMachineId(val machineId: String) : LoginIntent()

    /**
     * User submitted the login form (badge ID + machine ID).
     */
    data class SubmitLoginForm(val badgeId: String, val machineId: String) : LoginIntent()

    /**
     * User wants to retry workers login after failure.
     */
    data object RetryWorkersLogin : LoginIntent()

    // ========================================
    // Scanner-specific intents
    // ========================================

    /**
     * User clicked button to start scanning mode.
     */
    data object StartScanning : LoginIntent()

    /**
     * User wants to stop scanning mode.
     */
    data object StopScanning : LoginIntent()

    /**
     * User wants to toggle debug logs modal.
     */
    data object ToggleDebugLogs : LoginIntent()

    /**
     * User wants to copy debug logs to clipboard.
     */
    data object CopyLogs : LoginIntent()

    /**
     * User wants to clear debug logs.
     */
    data object ClearLogs : LoginIntent()

    // ========================================
    // Internal scanner event intents
    // (Triggered by scanner callbacks, not user actions)
    // ========================================

    /**
     * Internal: Scanner device appeared/detected.
     */
    internal data class OnScannerAppeared(val scannerId: Int, val name: String) : LoginIntent()

    /**
     * Internal: Scanner connected successfully.
     */
    internal data class OnScannerConnected(val scannerId: Int, val name: String) : LoginIntent()

    /**
     * Internal: Scanner disconnected.
     */
    internal data class OnScannerDisconnected(val scannerId: Int) : LoginIntent()

    /**
     * Internal: Scanner error occurred.
     */
    internal data class OnScannerError(val message: String) : LoginIntent()

    /**
     * Internal: Debug log message from scanner SDK.
     */
    internal data class OnLog(val level: LogLevel, val message: String) : LoginIntent()
}