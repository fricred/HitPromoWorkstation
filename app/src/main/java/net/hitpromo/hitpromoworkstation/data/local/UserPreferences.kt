package net.hitpromo.hitpromoworkstation.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data store for user preferences and session data.
 *
 * Uses EncryptedSharedPreferences for storing sensitive user data (userId, email, role)
 * with AES256_GCM encryption. Non-sensitive preferences (rememberMe) use standard storage.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "UserPreferences"
        private const val ENCRYPTED_PREFS_FILE = "encrypted_user_prefs"
        private const val STANDARD_PREFS_FILE = "standard_user_prefs"

        // Keys for encrypted preferences (sensitive data)
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_LAST_LOGIN_TIME = "last_login_time"

        // Keys for standard preferences (non-sensitive data)
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    /**
     * Encrypted SharedPreferences for sensitive user data.
     * Uses AES256_GCM encryption with Android Keystore.
     */
    private val encryptedPrefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                ENCRYPTED_PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create encrypted preferences, falling back to standard", e)
            // Fallback to standard SharedPreferences if encryption fails
            context.getSharedPreferences(ENCRYPTED_PREFS_FILE, Context.MODE_PRIVATE)
        }
    }

    /**
     * Standard SharedPreferences for non-sensitive data.
     */
    private val standardPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(STANDARD_PREFS_FILE, Context.MODE_PRIVATE)
    }

    // StateFlows for reactive data access
    private val _isLoggedIn = MutableStateFlow(encryptedPrefs.getBoolean(KEY_IS_LOGGED_IN, false))
    private val _userId = MutableStateFlow(encryptedPrefs.getString(KEY_USER_ID, null))
    private val _username = MutableStateFlow(encryptedPrefs.getString(KEY_USERNAME, null))
    private val _userEmail = MutableStateFlow(encryptedPrefs.getString(KEY_USER_EMAIL, null))
    private val _userRole = MutableStateFlow(encryptedPrefs.getString(KEY_USER_ROLE, null))
    private val _lastLoginTime = MutableStateFlow(encryptedPrefs.getString(KEY_LAST_LOGIN_TIME, null))
    private val _rememberMe = MutableStateFlow(standardPrefs.getBoolean(KEY_REMEMBER_ME, false))

    /**
     * Listener for encrypted preferences changes.
     */
    private val encryptedPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            KEY_IS_LOGGED_IN -> _isLoggedIn.value = prefs.getBoolean(key, false)
            KEY_USER_ID -> _userId.value = prefs.getString(key, null)
            KEY_USERNAME -> _username.value = prefs.getString(key, null)
            KEY_USER_EMAIL -> _userEmail.value = prefs.getString(key, null)
            KEY_USER_ROLE -> _userRole.value = prefs.getString(key, null)
            KEY_LAST_LOGIN_TIME -> _lastLoginTime.value = prefs.getString(key, null)
        }
    }

    /**
     * Listener for standard preferences changes.
     */
    private val standardPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            KEY_REMEMBER_ME -> _rememberMe.value = prefs.getBoolean(key, false)
        }
    }

    init {
        // Register preference change listeners
        encryptedPrefs.registerOnSharedPreferenceChangeListener(encryptedPrefsListener)
        standardPrefs.registerOnSharedPreferenceChangeListener(standardPrefsListener)
    }

    /**
     * Check if user is currently logged in (from encrypted storage).
     */
    val isLoggedIn: Flow<Boolean> = _isLoggedIn.asStateFlow()

    /**
     * Get stored user ID (from encrypted storage).
     */
    val userId: Flow<String?> = _userId.asStateFlow()

    /**
     * Get stored username (from encrypted storage).
     */
    val username: Flow<String?> = _username.asStateFlow()

    /**
     * Get stored user email (from encrypted storage).
     */
    val userEmail: Flow<String?> = _userEmail.asStateFlow()

    /**
     * Get stored user role (from encrypted storage).
     */
    val userRole: Flow<String?> = _userRole.asStateFlow()

    /**
     * Get last login time (from encrypted storage).
     */
    val lastLoginTime: Flow<String?> = _lastLoginTime.asStateFlow()

    /**
     * Get remember me preference (from standard storage).
     */
    val rememberMe: Flow<Boolean> = _rememberMe.asStateFlow()

    /**
     * Save user session data to encrypted storage.
     *
     * Stores sensitive information (userId, email, role) in EncryptedSharedPreferences
     * with AES256_GCM encryption.
     */
    suspend fun saveUserSession(
        userId: String,
        username: String,
        email: String,
        role: String,
        rememberMe: Boolean = false
    ) {
        try {
            encryptedPrefs.edit().apply {
                putBoolean(KEY_IS_LOGGED_IN, true)
                putString(KEY_USER_ID, userId)
                putString(KEY_USERNAME, username)
                putString(KEY_USER_EMAIL, email)
                putString(KEY_USER_ROLE, role)
                putString(KEY_LAST_LOGIN_TIME, System.currentTimeMillis().toString())
                apply()
            }

            // Store remember me in standard preferences (non-sensitive)
            standardPrefs.edit().apply {
                putBoolean(KEY_REMEMBER_ME, rememberMe)
                apply()
            }

            Log.d(TAG, "User session saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user session", e)
            throw e
        }
    }

    /**
     * Clear user session data from encrypted storage.
     *
     * Removes all sensitive user information while preserving the remember me preference.
     */
    suspend fun clearUserSession() {
        try {
            encryptedPrefs.edit().apply {
                putBoolean(KEY_IS_LOGGED_IN, false)
                remove(KEY_USER_ID)
                remove(KEY_USERNAME)
                remove(KEY_USER_EMAIL)
                remove(KEY_USER_ROLE)
                remove(KEY_LAST_LOGIN_TIME)
                apply()
            }

            Log.d(TAG, "User session cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear user session", e)
            throw e
        }
    }

    /**
     * Update remember me preference in standard storage.
     */
    suspend fun setRememberMe(remember: Boolean) {
        try {
            standardPrefs.edit().apply {
                putBoolean(KEY_REMEMBER_ME, remember)
                apply()
            }

            Log.d(TAG, "Remember me preference updated: $remember")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update remember me preference", e)
            throw e
        }
    }

    /**
     * Cleanup method to unregister preference listeners.
     * Should be called when the app is being destroyed.
     */
    fun cleanup() {
        try {
            encryptedPrefs.unregisterOnSharedPreferenceChangeListener(encryptedPrefsListener)
            standardPrefs.unregisterOnSharedPreferenceChangeListener(standardPrefsListener)
            Log.d(TAG, "Preference listeners unregistered")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cleanup preference listeners", e)
        }
    }
}