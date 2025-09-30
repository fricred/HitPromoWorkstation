package net.hitpromo.hitpromoworkstation.domain.repository

import kotlinx.coroutines.flow.Flow
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.model.AuthenticationState
import net.hitpromo.hitpromoworkstation.domain.model.PasswordResetResult
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

    /**
     * Confirm new password after receiving NEW_PASSWORD_REQUIRED challenge.
     *
     * @param sessionId The session ID from the NewPasswordRequired result
     * @param newPassword The new password to set
     * @return AuthResult indicating success or failure
     */
    suspend fun confirmNewPassword(sessionId: String, newPassword: String): AuthResult<User>

    /**
     * Cancel an active password change session.
     *
     * @param sessionId The session ID to cancel
     */
    fun cancelPasswordChangeSession(sessionId: String)

    /**
     * Request a password reset for the given username.
     *
     * Initiates the password reset flow by sending a verification code to the user's
     * registered email or phone number.
     *
     * @param username The username for which to request a password reset
     * @return PasswordResetResult indicating success (with delivery destination) or failure
     */
    suspend fun requestPasswordReset(username: String): PasswordResetResult

    /**
     * Confirm password reset with verification code and new password.
     *
     * Completes the password reset flow by validating the verification code and
     * setting the new password.
     *
     * @param username The username for which to reset the password
     * @param verificationCode The code received via email/SMS
     * @param newPassword The new password to set
     * @return PasswordResetResult indicating success or failure
     */
    suspend fun confirmPasswordReset(
        username: String,
        verificationCode: String,
        newPassword: String
    ): PasswordResetResult
}