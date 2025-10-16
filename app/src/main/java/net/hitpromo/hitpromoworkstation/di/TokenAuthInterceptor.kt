package net.hitpromo.hitpromoworkstation.di

import android.util.Log
import net.hitpromo.hitpromoworkstation.data.local.UserPreferences
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that automatically adds JWT token to API requests.
 *
 * Intercepts outgoing HTTP requests and adds the "Authorization: Bearer <token>"
 * header if a valid JWT token is stored in preferences. Skips requests that
 * already have an Authorization header.
 *
 * Token expiration is automatically checked, and expired tokens are not used.
 *
 * Note: Uses synchronous access to StateFlow values backed by SharedPreferences.
 * No coroutines are used as this runs on the network thread.
 */
class TokenAuthInterceptor(
    private val userPreferences: UserPreferences
) : Interceptor {

    companion object {
        private const val TAG = "TokenAuthInterceptor"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip adding token if Authorization header already present
        if (originalRequest.headers[AUTHORIZATION_HEADER] != null) {
            Log.d(TAG, "Authorization header already present, skipping token injection")
            return chain.proceed(originalRequest)
        }

        // Get token synchronously from preferences
        val token = try {
            userPreferences.getTokenSync()
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving valid token", e)
            null
        }

        return if (token != null) {
            val newRequest = originalRequest.newBuilder()
                .addHeader(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
                .build()

            chain.proceed(newRequest)
        } else {
            Log.d(TAG, "No valid token available, proceeding without authentication header")
            chain.proceed(originalRequest)
        }
    }
}
