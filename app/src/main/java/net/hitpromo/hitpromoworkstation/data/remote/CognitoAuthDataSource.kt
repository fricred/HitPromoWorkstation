package net.hitpromo.hitpromoworkstation.data.remote

import android.util.Log
import com.amplifyframework.auth.AuthException
import net.hitpromo.hitpromoworkstation.BuildConfig
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.exceptions.invalidstate.SignedInException
import com.amplifyframework.auth.exceptions.NotAuthorizedException
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import net.hitpromo.hitpromoworkstation.R
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import net.hitpromo.hitpromoworkstation.data.remote.dto.CognitoUserDto
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.util.NetworkMonitor
import net.hitpromo.hitpromoworkstation.util.StringProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Data source for AWS Cognito authentication operations.
 *
 * This class handles direct communication with AWS Cognito services
 * using AWS Amplify SDK with proper error handling, thread safety,
 * and cancellation support.
 */
@Singleton
class CognitoAuthDataSource @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val stringProvider: StringProvider
) {

    private val _currentUser = MutableStateFlow<CognitoUserDto?>(null)
    val currentUser: StateFlow<CognitoUserDto?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Mutex to protect StateFlow updates for thread safety
    private val stateMutex = Mutex()

    /**
     * Track active authentication calls to prevent processing results after cancellation.
     * Uses ConcurrentHashMap for thread-safe operations without additional locking.
     */
    private val activeCalls = ConcurrentHashMap.newKeySet<String>()

    /**
     * Update authentication state in a thread-safe manner.
     *
     * @param user The current user or null if not authenticated
     * @param isAuth Whether the user is authenticated
     */
    private suspend fun updateAuthState(user: CognitoUserDto?, isAuth: Boolean) {
        stateMutex.withLock {
            _currentUser.value = user
            _isAuthenticated.value = isAuth
        }
    }

    /**
     * Sign in with AWS Cognito.
     *
     * @param username The user's username
     * @param password The user's password
     * @return AuthResult with CognitoUserDto or error
     */
    suspend fun signIn(username: String, password: String): AuthResult<CognitoUserDto> {
        return try {
            // Check network connectivity
            if (!networkMonitor.isNetworkAvailable()) {
                return AuthResult.Error(stringProvider.getString(R.string.error_network_unavailable))
            }

            // Only log username in debug builds to prevent sensitive information leakage
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Attempting sign-in for user: $username")
            } else {
                Log.d(TAG, "Attempting sign-in")
            }

            // Generate unique call ID for tracking
            val callId = UUID.randomUUID().toString()
            activeCalls.add(callId)

            // Perform Cognito sign-in with timeout
            val signInResult = withTimeout(SIGN_IN_TIMEOUT_MS) {
                suspendCancellableCoroutine<Pair<Any?, AuthException?>> { continuation ->
                    val signInCall = Amplify.Auth.signIn(
                        username,
                        password,
                        { result ->
                            // Check if this call is still active before processing result
                            if (activeCalls.contains(callId)) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "Sign-in successful for user: $username")
                                } else {
                                    Log.d(TAG, "Sign-in successful")
                                }
                                if (continuation.isActive) {
                                    continuation.resume(Pair(result, null))
                                }
                            } else {
                                Log.d(TAG, "Ignoring sign-in result for cancelled call")
                            }
                        },
                        { error ->
                            // Check if this call is still active before processing error
                            if (activeCalls.contains(callId)) {
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "Sign-in failed for user: $username", error)
                                } else {
                                    Log.e(TAG, "Sign-in failed", error)
                                }
                                if (continuation.isActive) {
                                    continuation.resume(Pair(null, error))
                                }
                            } else {
                                Log.d(TAG, "Ignoring sign-in error for cancelled call")
                            }
                        }
                    )

                    continuation.invokeOnCancellation {
                        // Remove from active calls to prevent processing results
                        activeCalls.remove(callId)
                        if (BuildConfig.DEBUG) {
                            Log.w(TAG, "Sign-in cancelled for user: $username")
                        } else {
                            Log.w(TAG, "Sign-in cancelled")
                        }
                        // Note: Amplify doesn't provide a cancellation mechanism for auth operations
                        // The operation will complete on the server side but we won't process the result
                    }
                }
            }

            // Clean up call tracking after successful completion
            activeCalls.remove(callId)

            // Check if there was an error
            val error = signInResult.second
            if (error != null) {
                // Check if user is already signed in
                if (error is SignedInException) {
                    Log.d(TAG, "User already signed in, fetching current user attributes")
                    val user = fetchCurrentUserAttributes()
                    if (user != null) {
                        updateAuthState(user, true)
                        return AuthResult.Success(user)
                    } else {
                        return AuthResult.Error(stringProvider.getString(R.string.error_fetch_current_user))
                    }
                }
                // Throw the error to be caught by the catch blocks
                throw error
            }

            val result = signInResult.first
            if (result != null) {
                // Safely cast and handle the result
                when (result) {
                    is com.amplifyframework.auth.result.AuthSignInResult -> {
                        if (result.isSignedIn) {
                            // Fetch user attributes after successful sign-in
                            val user = fetchCurrentUserAttributes()
                            if (user != null) {
                                updateAuthState(user, true)
                                AuthResult.Success(user)
                            } else {
                                AuthResult.Error(stringProvider.getString(R.string.error_fetch_user_attributes))
                            }
                        } else {
                            // Check what additional step is required
                            val nextStep = result.nextStep
                            Log.w(TAG, "Sign-in requires additional step: ${nextStep?.signInStep}")

                            val errorMessage = when (nextStep?.signInStep?.toString()) {
                                "CONFIRM_SIGN_IN_WITH_NEW_PASSWORD" ->
                                    stringProvider.getString(R.string.error_change_password_required)
                                "CONFIRM_SIGN_IN_WITH_CUSTOM_CHALLENGE" ->
                                    stringProvider.getString(R.string.error_custom_challenge_required)
                                "CONFIRM_SIGN_IN_WITH_SMS_MFA_CODE" ->
                                    stringProvider.getString(R.string.error_sms_mfa_required)
                                "CONFIRM_SIGN_IN_WITH_TOTP_CODE" ->
                                    stringProvider.getString(R.string.error_totp_required)
                                "CONTINUE_SIGN_IN_WITH_MFA_SELECTION" ->
                                    stringProvider.getString(R.string.error_mfa_selection_required)
                                "CONTINUE_SIGN_IN_WITH_TOTP_SETUP" ->
                                    stringProvider.getString(R.string.error_totp_setup_required)
                                "CONFIRM_SIGN_UP" ->
                                    stringProvider.getString(R.string.error_confirm_sign_up_required)
                                "RESET_PASSWORD" ->
                                    stringProvider.getString(R.string.error_reset_password_required)
                                else ->
                                    stringProvider.getString(
                                        R.string.error_additional_steps_required,
                                        nextStep?.signInStep ?: "Unknown"
                                    )
                            }

                            AuthResult.Error(errorMessage)
                        }
                    }
                    else -> {
                        Log.e(TAG, "Unexpected result type: ${result::class.java.simpleName}")
                        AuthResult.Error(stringProvider.getString(R.string.error_sign_in_unknown))
                    }
                }
            } else {
                AuthResult.Error(stringProvider.getString(R.string.error_sign_in_unknown))
            }

        } catch (e: SignedInException) {
            // User is already signed in - silently fetch current user and return success
            Log.d(TAG, "User already signed in, fetching current user attributes")
            val user = fetchCurrentUserAttributes()
            if (user != null) {
                updateAuthState(user, true)
                AuthResult.Success(user)
            } else {
                Log.e(TAG, "Failed to fetch user attributes for already signed-in user")
                AuthResult.Error(stringProvider.getString(R.string.error_fetch_current_user))
            }
        } catch (e: NotAuthorizedException) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Invalid credentials for user: $username", e)
            } else {
                Log.w(TAG, "Invalid credentials", e)
            }
            AuthResult.Error(stringProvider.getString(R.string.error_invalid_credentials), e)
        } catch (e: AuthException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Authentication error for user: $username", e)
            } else {
                Log.e(TAG, "Authentication error", e)
            }
            // Check exception message for specific errors
            val errorMessage = when {
                e.message?.contains("UserNotFoundException", ignoreCase = true) == true ->
                    stringProvider.getString(R.string.error_user_not_found)
                e.message?.contains("UserNotConfirmedException", ignoreCase = true) == true ->
                    stringProvider.getString(R.string.error_email_not_verified)
                e.message?.contains("InvalidParameterException", ignoreCase = true) == true ->
                    stringProvider.getString(R.string.error_invalid_parameter)
                else -> stringProvider.getString(R.string.error_authentication_failed)
            }
            AuthResult.Error(errorMessage, e)
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Sign-in timeout for user: $username", e)
            } else {
                Log.e(TAG, "Sign-in timeout", e)
            }
            AuthResult.Error(stringProvider.getString(R.string.error_sign_in_timeout), e)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Unexpected error during sign-in for user: $username", e)
            } else {
                Log.e(TAG, "Unexpected error during sign-in", e)
            }
            AuthResult.Error(stringProvider.getString(R.string.error_network_unavailable), e)
        }
    }

    /**
     * Sign out from AWS Cognito.
     *
     * @return AuthResult indicating success or failure
     */
    suspend fun signOut(): AuthResult<Unit> {
        return try {
            Log.d(TAG, "Signing out user")

            // Generate unique call ID for tracking
            val callId = UUID.randomUUID().toString()
            activeCalls.add(callId)

            // Perform Cognito sign-out with timeout
            withTimeout(AUTH_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    val signOutCall = Amplify.Auth.signOut { signOutResult ->
                        // Check if this call is still active before processing result
                        if (activeCalls.contains(callId)) {
                            Log.d(TAG, "Sign-out completed")
                            if (continuation.isActive) {
                                continuation.resume(Unit)
                            }
                        } else {
                            Log.d(TAG, "Ignoring sign-out result for cancelled call")
                        }
                    }

                    continuation.invokeOnCancellation {
                        // Remove from active calls to prevent processing results
                        activeCalls.remove(callId)
                        Log.w(TAG, "Sign-out cancelled")
                        // Note: Amplify doesn't provide cancellation for auth operations
                    }
                }
            }

            // Clean up call tracking after successful completion
            activeCalls.remove(callId)

            updateAuthState(null, false)
            AuthResult.Success(Unit)

        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e(TAG, "Sign-out timeout", e)
            // Still clear local state even if remote sign-out times out
            updateAuthState(null, false)
            AuthResult.Error(stringProvider.getString(R.string.error_operation_timeout), e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during sign-out", e)
            // Still clear local state even if remote sign-out fails
            updateAuthState(null, false)
            AuthResult.Error(
                stringProvider.getString(R.string.error_sign_out_failed, e.message ?: "Unknown"),
                e
            )
        }
    }

    /**
     * Get the current authenticated user.
     *
     * @return Current CognitoUserDto or null
     */
    suspend fun getCurrentUser(): CognitoUserDto? {
        return try {
            stateMutex.withLock {
                if (_currentUser.value != null) {
                    _currentUser.value
                } else {
                    // Try to fetch from Amplify
                    fetchCurrentUserAttributes()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user", e)
            null
        }
    }

    /**
     * Validate the current session.
     *
     * @return true if session is valid, false otherwise
     */
    suspend fun validateSession(): Boolean {
        return try {
            // Check network connectivity
            if (!networkMonitor.isNetworkAvailable()) {
                Log.w(TAG, "Cannot validate session: network unavailable")
                // Return current auth state without making network call
                return _isAuthenticated.value
            }

            // Generate unique call ID for tracking
            val callId = UUID.randomUUID().toString()
            activeCalls.add(callId)

            val authSession = withTimeout(AUTH_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    val fetchCall = Amplify.Auth.fetchAuthSession(
                        { session ->
                            // Check if this call is still active before processing result
                            if (activeCalls.contains(callId)) {
                                if (continuation.isActive) {
                                    continuation.resume(session)
                                }
                            } else {
                                Log.d(TAG, "Ignoring auth session result for cancelled call")
                            }
                        },
                        { error ->
                            // Check if this call is still active before processing error
                            if (activeCalls.contains(callId)) {
                                Log.e(TAG, "Failed to fetch auth session", error)
                                if (continuation.isActive) {
                                    continuation.resume(null)
                                }
                            } else {
                                Log.d(TAG, "Ignoring auth session error for cancelled call")
                            }
                        }
                    )

                    continuation.invokeOnCancellation {
                        // Remove from active calls to prevent processing results
                        activeCalls.remove(callId)
                        Log.w(TAG, "Session validation cancelled")
                    }
                }
            }

            // Clean up call tracking after successful completion
            activeCalls.remove(callId)

            val isValid = authSession?.isSignedIn == true
            if (isValid) {
                stateMutex.withLock {
                    if (_currentUser.value == null) {
                        // Session is valid but we don't have user data, fetch it
                        fetchCurrentUserAttributes()
                    }
                }
            }

            updateAuthState(_currentUser.value, isValid)
            isValid

        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e(TAG, "Session validation timeout", e)
            updateAuthState(null, false)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error validating session", e)
            updateAuthState(null, false)
            false
        }
    }

    /**
     * Refresh the authentication session.
     *
     * Actively triggers token refresh by fetching the Cognito auth session and
     * extracting tokens. This forces AWS Cognito to refresh expired tokens.
     *
     * @return AuthResult with refreshed user data
     */
    suspend fun refreshSession(): AuthResult<CognitoUserDto> {
        return try {
            // Check network connectivity
            if (!networkMonitor.isNetworkAvailable()) {
                return AuthResult.Error(stringProvider.getString(R.string.error_network_unavailable))
            }

            Log.d(TAG, "Refreshing session and tokens")

            // Generate unique call ID for tracking
            val callId = UUID.randomUUID().toString()
            activeCalls.add(callId)

            // Fetch Cognito auth session to trigger token refresh
            val authSession = withTimeout(AUTH_TIMEOUT_MS) {
                suspendCancellableCoroutine<AWSCognitoAuthSession?> { continuation ->
                    val fetchCall = Amplify.Auth.fetchAuthSession(
                        { session ->
                            // Check if this call is still active before processing result
                            if (activeCalls.contains(callId)) {
                                if (continuation.isActive) {
                                    // Cast to AWSCognitoAuthSession to access tokens
                                    continuation.resume(session as? AWSCognitoAuthSession)
                                }
                            } else {
                                Log.d(TAG, "Ignoring session refresh result for cancelled call")
                            }
                        },
                        { error ->
                            // Check if this call is still active before processing error
                            if (activeCalls.contains(callId)) {
                                Log.e(TAG, "Failed to fetch auth session for refresh", error)
                                if (continuation.isActive) {
                                    continuation.resume(null)
                                }
                            } else {
                                Log.d(TAG, "Ignoring session refresh error for cancelled call")
                            }
                        }
                    )

                    continuation.invokeOnCancellation {
                        // Remove from active calls to prevent processing results
                        activeCalls.remove(callId)
                        Log.w(TAG, "Session refresh cancelled")
                    }
                }
            }

            // Clean up call tracking after successful completion
            activeCalls.remove(callId)

            if (authSession == null || !authSession.isSignedIn) {
                Log.w(TAG, "No active session to refresh")
                updateAuthState(null, false)
                return AuthResult.Error(stringProvider.getString(R.string.error_no_active_session))
            }

            // Extract and validate user pool tokens to ensure they're refreshed
            val tokensResult = authSession.userPoolTokensResult
            when (tokensResult.type) {
                com.amplifyframework.auth.result.AuthSessionResult.Type.SUCCESS -> {
                    val tokens = tokensResult.value
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Tokens refreshed successfully - Access token valid")
                    }

                    // Fetch updated user attributes with refreshed session
                    val user = fetchCurrentUserAttributes()
                    if (user != null) {
                        updateAuthState(user, true)
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Session refreshed for user: ${user.username}")
                        } else {
                            Log.d(TAG, "Session refreshed successfully")
                        }
                        AuthResult.Success(user)
                    } else {
                        Log.w(TAG, "Tokens refreshed but failed to fetch user attributes")
                        AuthResult.Error(stringProvider.getString(R.string.error_fetch_user_attributes))
                    }
                }
                com.amplifyframework.auth.result.AuthSessionResult.Type.FAILURE -> {
                    val error = tokensResult.error
                    Log.e(TAG, "Failed to refresh tokens", error)
                    updateAuthState(null, false)
                    AuthResult.Error(
                        stringProvider.getString(R.string.error_session_refresh_failed, error?.message ?: "Token refresh failed"),
                        error
                    )
                }
                else -> {
                    Log.w(TAG, "Unexpected tokens result type: ${tokensResult.type}")
                    updateAuthState(null, false)
                    AuthResult.Error(stringProvider.getString(R.string.error_session_refresh_failed, "Unexpected result type"))
                }
            }

        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e(TAG, "Session refresh timeout", e)
            updateAuthState(null, false)
            AuthResult.Error(stringProvider.getString(R.string.error_operation_timeout), e)
        } catch (e: Exception) {
            Log.e(TAG, "Session refresh failed", e)
            updateAuthState(null, false)
            AuthResult.Error(
                stringProvider.getString(R.string.error_session_refresh_failed, e.message ?: "Unknown"),
                e
            )
        }
    }

    /**
     * Fetch current user attributes from Cognito.
     *
     * @return CognitoUserDto with user data or null if failed
     */
    private suspend fun fetchCurrentUserAttributes(): CognitoUserDto? {
        return try {
            // Generate unique call IDs for tracking
            val getUserCallId = UUID.randomUUID().toString()
            val fetchAttrsCallId = UUID.randomUUID().toString()

            activeCalls.add(getUserCallId)

            // Get current user with timeout
            val authUser = withTimeout(AUTH_TIMEOUT_MS) {
                suspendCancellableCoroutine<com.amplifyframework.auth.AuthUser?> { continuation ->
                    val getCurrentUserCall = Amplify.Auth.getCurrentUser(
                        { user ->
                            // Check if this call is still active before processing result
                            if (activeCalls.contains(getUserCallId)) {
                                if (continuation.isActive) {
                                    continuation.resume(user)
                                }
                            } else {
                                Log.d(TAG, "Ignoring get current user result for cancelled call")
                            }
                        },
                        { error ->
                            // Check if this call is still active before processing error
                            if (activeCalls.contains(getUserCallId)) {
                                Log.e(TAG, "Failed to get current user", error)
                                if (continuation.isActive) {
                                    continuation.resume(null)
                                }
                            } else {
                                Log.d(TAG, "Ignoring get current user error for cancelled call")
                            }
                        }
                    )

                    continuation.invokeOnCancellation {
                        // Remove from active calls to prevent processing results
                        activeCalls.remove(getUserCallId)
                        Log.w(TAG, "Get current user cancelled")
                    }
                }
            }.also {
                // Clean up call tracking after completion
                activeCalls.remove(getUserCallId)
            } ?: return null

            activeCalls.add(fetchAttrsCallId)

            // Fetch user attributes with timeout
            val attributes = withTimeout(AUTH_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    val fetchAttrsCall = Amplify.Auth.fetchUserAttributes(
                        { attrs ->
                            // Check if this call is still active before processing result
                            if (activeCalls.contains(fetchAttrsCallId)) {
                                if (continuation.isActive) {
                                    continuation.resume(attrs)
                                }
                            } else {
                                Log.d(TAG, "Ignoring fetch attributes result for cancelled call")
                            }
                        },
                        { error ->
                            // Check if this call is still active before processing error
                            if (activeCalls.contains(fetchAttrsCallId)) {
                                Log.e(TAG, "Failed to fetch user attributes", error)
                                if (continuation.isActive) {
                                    continuation.resume(emptyList())
                                }
                            } else {
                                Log.d(TAG, "Ignoring fetch attributes error for cancelled call")
                            }
                        }
                    )

                    continuation.invokeOnCancellation {
                        // Remove from active calls to prevent processing results
                        activeCalls.remove(fetchAttrsCallId)
                        Log.w(TAG, "Fetch user attributes cancelled")
                    }
                }
            }.also {
                // Clean up call tracking after completion
                activeCalls.remove(fetchAttrsCallId)
            }

            // Convert attributes to map
            val attributesMap = attributes.associate { attr ->
                attr.key.keyString to attr.value
            }

            // Get username - safely handle property access
            val usernameValue = getUsername(authUser, attributesMap)
            val userIdValue = getUserId(authUser, usernameValue)

            // Create CognitoUserDto
            CognitoUserDto(
                username = usernameValue,
                userId = userIdValue,
                email = attributesMap[AuthUserAttributeKey.email().keyString] ?: "",
                attributes = attributesMap
            )

        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.e(TAG, "Timeout fetching user attributes", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user attributes", e)
            null
        }
    }

    /**
     * Safely get username from AuthUser.
     *
     * @param authUser The AuthUser instance
     * @param attributesMap The user attributes map as fallback
     * @return The username string
     */
    private fun getUsername(
        authUser: com.amplifyframework.auth.AuthUser,
        attributesMap: Map<String, String>
    ): String {
        return try {
            authUser.username
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get username property, trying userId", e)
            try {
                authUser.userId
            } catch (e2: Exception) {
                Log.w(TAG, "Failed to get userId property, using email", e2)
                attributesMap[AuthUserAttributeKey.email().keyString] ?: ""
            }
        }
    }

    /**
     * Safely get userId from AuthUser.
     *
     * @param authUser The AuthUser instance
     * @param fallbackUsername The username to use as fallback
     * @return The userId string
     */
    private fun getUserId(
        authUser: com.amplifyframework.auth.AuthUser,
        fallbackUsername: String
    ): String {
        return try {
            authUser.userId
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get userId property, using username as fallback", e)
            fallbackUsername
        }
    }

    companion object {
        private const val TAG = "CognitoAuthDataSource"
        private const val AUTH_TIMEOUT_MS = 30_000L // 30 seconds
        private const val SIGN_IN_TIMEOUT_MS = 45_000L // 45 seconds for sign-in
    }
}