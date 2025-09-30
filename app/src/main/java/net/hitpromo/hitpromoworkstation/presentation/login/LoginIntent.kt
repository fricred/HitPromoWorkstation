package net.hitpromo.hitpromoworkstation.presentation.login

/**
 * MVI Intent sealed class representing all possible user actions on the login screen.
 */
sealed class LoginIntent {
    /**
     * User wants to sign in with credentials.
     */
    data class SignIn(val username: String, val password: String) : LoginIntent()

    /**
     * User wants to sign out.
     */
    object SignOut : LoginIntent()

    /**
     * User wants to toggle remember me preference.
     */
    data class ToggleRememberMe(val remember: Boolean) : LoginIntent()

    /**
     * User clicked forgot password.
     */
    object ForgotPassword : LoginIntent()

    /**
     * Clear any error messages.
     */
    object ClearError : LoginIntent()

    /**
     * Refresh the authentication session.
     */
    object RefreshSession : LoginIntent()

    /**
     * Clear password change state after successful password change.
     */
    object ClearPasswordChangeState : LoginIntent()
}