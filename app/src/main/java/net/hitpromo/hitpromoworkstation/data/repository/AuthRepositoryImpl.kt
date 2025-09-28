package net.hitpromo.hitpromoworkstation.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import net.hitpromo.hitpromoworkstation.data.local.UserPreferences
import net.hitpromo.hitpromoworkstation.data.remote.CognitoAuthDataSource
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.model.AuthenticationState
import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.model.UserRole
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository using AWS Cognito and local preferences.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val cognitoDataSource: CognitoAuthDataSource,
    private val userPreferences: UserPreferences
) : AuthRepository {

    private val _authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.Unauthenticated)
    override val authenticationState: Flow<AuthenticationState> = _authenticationState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    init {
        // Initialize authentication state from local storage
        initializeAuthState()
    }

    override suspend fun signIn(username: String, password: String): AuthResult<User> {
        return try {
            _authenticationState.value = AuthenticationState.Loading

            val result = cognitoDataSource.signIn(username, password)

            when (result) {
                is AuthResult.Success -> {
                    val user = result.data.toDomainModel()
                    _currentUser.value = user
                    _authenticationState.value = AuthenticationState.Authenticated(user)

                    // Save session to local storage
                    userPreferences.saveUserSession(
                        userId = user.id,
                        username = user.username,
                        email = user.email,
                        role = user.role.name
                    )

                    AuthResult.Success(user)
                }
                is AuthResult.Error -> {
                    _authenticationState.value = AuthenticationState.Error(result.message, result.cause)
                    AuthResult.Error(result.message, result.cause)
                }
                is AuthResult.Loading -> AuthResult.Loading
            }

        } catch (e: Exception) {
            val errorMessage = "Sign-in failed: ${e.message}"
            _authenticationState.value = AuthenticationState.Error(errorMessage, e)
            AuthResult.Error(errorMessage, e)
        }
    }

    override suspend fun signOut(): AuthResult<Unit> {
        return try {
            _authenticationState.value = AuthenticationState.Loading

            val result = cognitoDataSource.signOut()

            when (result) {
                is AuthResult.Success -> {
                    _currentUser.value = null
                    _authenticationState.value = AuthenticationState.Unauthenticated

                    // Clear local session
                    userPreferences.clearUserSession()

                    AuthResult.Success(Unit)
                }
                is AuthResult.Error -> {
                    AuthResult.Error(result.message, result.cause)
                }
                is AuthResult.Loading -> AuthResult.Loading
            }

        } catch (e: Exception) {
            AuthResult.Error("Sign-out failed: ${e.message}", e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return cognitoDataSource.validateSession() && userPreferences.isLoggedIn.first()
    }

    override suspend fun getCurrentUser(): User? {
        return _currentUser.value
    }

    override suspend fun refreshSession(): AuthResult<User> {
        return try {
            val result = cognitoDataSource.refreshSession()

            when (result) {
                is AuthResult.Success -> {
                    val user = result.data.toDomainModel()
                    _currentUser.value = user
                    _authenticationState.value = AuthenticationState.Authenticated(user)
                    AuthResult.Success(user)
                }
                is AuthResult.Error -> {
                    _currentUser.value = null
                    _authenticationState.value = AuthenticationState.Error(result.message, result.cause)
                    userPreferences.clearUserSession()
                    AuthResult.Error(result.message, result.cause)
                }
                is AuthResult.Loading -> AuthResult.Loading
            }

        } catch (e: Exception) {
            AuthResult.Error("Session refresh failed: ${e.message}", e)
        }
    }

    override suspend fun validateSession(): Boolean {
        return try {
            if (cognitoDataSource.validateSession()) {
                // Validate against local storage as well
                val isLoggedIn = userPreferences.isLoggedIn.first()
                val hasUserId = userPreferences.userId.first() != null

                if (isLoggedIn && hasUserId && _currentUser.value == null) {
                    // Restore user from local storage
                    restoreUserFromPreferences()
                }

                isLoggedIn && hasUserId
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Initialize authentication state from local storage on app start.
     */
    private fun initializeAuthState() {
        // This would typically be called in a coroutine scope
        // For now, we'll check session validity when needed
    }

    /**
     * Restore user data from local preferences.
     */
    private suspend fun restoreUserFromPreferences() {
        try {
            val userId = userPreferences.userId.first()
            val username = userPreferences.username.first()
            val email = userPreferences.userEmail.first()
            val roleString = userPreferences.userRole.first()
            val lastLoginString = userPreferences.lastLoginTime.first()

            if (userId != null && username != null && email != null && roleString != null) {
                val role = try {
                    UserRole.valueOf(roleString)
                } catch (e: IllegalArgumentException) {
                    UserRole.OPERATOR
                }

                val lastLoginTime = lastLoginString?.toLongOrNull()

                val user = User(
                    id = userId,
                    username = username,
                    email = email,
                    role = role,
                    isActive = true,
                    lastLoginTime = lastLoginTime
                )

                _currentUser.value = user
                _authenticationState.value = AuthenticationState.Authenticated(user)
            }
        } catch (e: Exception) {
            // If restoration fails, clear the session
            userPreferences.clearUserSession()
            _currentUser.value = null
            _authenticationState.value = AuthenticationState.Unauthenticated
        }
    }
}