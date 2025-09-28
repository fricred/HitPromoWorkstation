package net.hitpromo.hitpromoworkstation.domain.repository

import kotlinx.coroutines.flow.Flow
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.model.AuthenticationState
import net.hitpromo.hitpromoworkstation.domain.model.User

/**
 * Repository interface for authentication operations.
 *
 * This interface defines the contract for authentication-related data operations,
 * abstracting the implementation details from the domain layer.
 */
interface AuthRepository {

    /**
     * Observable authentication state.
     */
    val authenticationState: Flow<AuthenticationState>

    /**
     * Current authenticated user, if any.
     */
    val currentUser: Flow<User?>

    /**
     * Sign in with username and password.
     *
     * @param username The user's username
     * @param password The user's password
     * @return AuthResult indicating success or failure
     */
    suspend fun signIn(username: String, password: String): AuthResult<User>

    /**
     * Sign out the current user.
     *
     * @return AuthResult indicating success or failure
     */
    suspend fun signOut(): AuthResult<Unit>

    /**
     * Check if a user is currently authenticated.
     *
     * @return true if user is authenticated, false otherwise
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Get the current authenticated user.
     *
     * @return The current user or null if not authenticated
     */
    suspend fun getCurrentUser(): User?

    /**
     * Refresh the authentication session.
     *
     * @return AuthResult indicating success or failure
     */
    suspend fun refreshSession(): AuthResult<User>

    /**
     * Validate the current session.
     *
     * @return true if session is valid, false otherwise
     */
    suspend fun validateSession(): Boolean
}