package net.hitpromo.hitpromoworkstation.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data store for user preferences and session data.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val LAST_LOGIN_TIME = stringPreferencesKey("last_login_time")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    /**
     * Check if user is currently logged in.
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    /**
     * Get stored user ID.
     */
    val userId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    /**
     * Get stored username.
     */
    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USERNAME]
    }

    /**
     * Get stored user email.
     */
    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    /**
     * Get stored user role.
     */
    val userRole: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ROLE]
    }

    /**
     * Get last login time.
     */
    val lastLoginTime: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_LOGIN_TIME]
    }

    /**
     * Get remember me preference.
     */
    val rememberMe: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMEMBER_ME] ?: false
    }

    /**
     * Save user session data.
     */
    suspend fun saveUserSession(
        userId: String,
        username: String,
        email: String,
        role: String,
        rememberMe: Boolean = false
    ) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
            preferences[USERNAME] = username
            preferences[USER_EMAIL] = email
            preferences[USER_ROLE] = role
            preferences[LAST_LOGIN_TIME] = System.currentTimeMillis().toString()
            preferences[REMEMBER_ME] = rememberMe
        }
    }

    /**
     * Clear user session data.
     */
    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
            preferences.remove(USER_ID)
            preferences.remove(USERNAME)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_ROLE)
            preferences.remove(LAST_LOGIN_TIME)
            // Keep REMEMBER_ME preference
        }
    }

    /**
     * Update remember me preference.
     */
    suspend fun setRememberMe(remember: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMEMBER_ME] = remember
        }
    }
}