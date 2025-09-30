package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Represents the authentication state of the application.
 */
sealed class AuthenticationState {
    /**
     * User is not authenticated.
     */
    object Unauthenticated : AuthenticationState()

    /**
     * Authentication is in progress.
     */
    object Loading : AuthenticationState()

    /**
     * User is successfully authenticated.
     */
    data class Authenticated(val user: User) : AuthenticationState()

    /**
     * Authentication failed.
     */
    data class Error(val message: String, val cause: Throwable? = null) : AuthenticationState()
}

/**
 * Result wrapper for authentication operations.
 */
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : AuthResult<Nothing>()
    data class NewPasswordRequired(val username: String, val sessionId: String) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}