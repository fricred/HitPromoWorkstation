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
        val requestUrl = originalRequest.url.toString()

        Log.d(TAG, "Intercepting request to: $requestUrl")

        // Skip adding token if Authorization header already present
        if (originalRequest.headers[AUTHORIZATION_HEADER] != null) {
            Log.d(TAG, "Authorization header already present, skipping token injection for: $requestUrl")
            return chain.proceed(originalRequest)
        }

        // Get token synchronously from preferences
        val token = try {
            userPreferences.getTokenSync()
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving valid token for $requestUrl", e)
            null
        }

        return if (token != null) {
            val tokenLength = token.length
            val tokenPreview = if (tokenLength > 20) token.substring(0, 20) + "..." else token

            Log.d(TAG, "Adding Authorization header for: $requestUrl (token length: $tokenLength)")
            Log.d(TAG, "Token preview: $tokenPreview")

            val newRequest = originalRequest.newBuilder()
                .addHeader(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
                .build()

            Log.d(TAG, "Proceeding with authenticated request to: $requestUrl")
            chain.proceed(newRequest)
        } else {
            Log.w(TAG, "No valid token available, proceeding WITHOUT authentication header for: $requestUrl")
            Log.w(TAG, "Token retrieval returned null - token may be expired or not yet set")
            chain.proceed(originalRequest)
        }
    }
}
