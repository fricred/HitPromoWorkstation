package net.hitpromo.hitpromoworkstation.data.repository

import android.util.Log
import net.hitpromo.hitpromoworkstation.data.local.UserPreferences
import net.hitpromo.hitpromoworkstation.data.model.BadgeLookupRequest
import net.hitpromo.hitpromoworkstation.data.model.BadgeLookupResponse
import net.hitpromo.hitpromoworkstation.data.remote.BadgeApiService
import net.hitpromo.hitpromoworkstation.domain.repository.BadgeAuthRepository
import net.hitpromo.hitpromoworkstation.util.JwtTokenManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of BadgeAuthRepository using Retrofit API service.
 *
 * Handles badge authentication by calling the HR data badge scan lookup API
 * and storing the returned JWT token for subsequent API requests.
 */
@Singleton
class BadgeAuthRepositoryImpl @Inject constructor(
    private val badgeApiService: BadgeApiService,
    private val userPreferences: UserPreferences
) : BadgeAuthRepository {

    companion object {
        private const val TAG = "BadgeAuthRepository"
    }

    override suspend fun authenticateWithBadge(badgeId: String): Result<BadgeLookupResponse> {
        return try {
            Log.d(TAG, "=== Starting Badge Authentication ===")
            Log.d(TAG, "Badge ID: $badgeId")

            val request = BadgeLookupRequest(operatorId = badgeId)
            Log.d(TAG, "Making API call to badge endpoint...")

            val response = badgeApiService.lookupBadge(request)
            Log.d(TAG, "Badge API response received")
            Log.d(TAG, "Response success: ${response.success}")
            Log.d(TAG, "Response message: ${response.message}")

            if (response.success) {
                Log.d(TAG, "Badge authentication successful from server")

                // Store JWT token if present in response
                if (!response.token.isNullOrEmpty()) {
                    try {
                        val tokenLength = response.token.length
                        Log.d(TAG, "JWT token received from server (length: $tokenLength)")

                        // Decode token to get expiration time
                        val claims = JwtTokenManager.decodeClaims(response.token)
                        Log.d(TAG, "Token decoded successfully")
                        Log.d(TAG, "Token claims - worker_id: ${claims?.workerId}, exp: ${claims?.exp}")

                        if (claims != null && claims.exp != null) {
                            val expirationTimeMs = claims.exp * 1000L
                            val expirationSeconds = (expirationTimeMs - System.currentTimeMillis()) / 1000L
                            Log.d(TAG, "Token expires in $expirationSeconds seconds")

                            userPreferences.saveJwtToken(response.token, expirationTimeMs)
                            Log.d(TAG, "JWT token stored successfully in UserPreferences")
                            Log.d(TAG, "=== Badge Authentication Complete ===")
                        } else {
                            Log.e(TAG, "Invalid JWT token - cannot extract expiration claims")
                            Log.e(TAG, "Claims: $claims")
                            // Fail authentication if token is invalid
                            return Result.failure(Exception("Invalid JWT token received from server: missing expiration"))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "JWT token validation failed", e)
                        Log.e(TAG, "Error details: ${e.javaClass.simpleName} - ${e.message}")
                        // Fail authentication if token validation fails
                        return Result.failure(Exception("JWT token validation error", e))
                    }
                } else {
                    Log.w(TAG, "No JWT token received in badge authentication response")
                }

                Result.success(response)
            } else {
                val errorMessage = response.message ?: "Badge authentication failed"
                Log.w(TAG, "Badge authentication failed: $errorMessage")
                Log.w(TAG, "=== Badge Authentication Failed ===")
                Result.failure(Exception(errorMessage))
            }

        } catch (e: Exception) {
            Log.e(TAG, "=== Badge Authentication Error ===", e)
            Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Error message: ${e.message}")
            Result.failure(e)
        }
    }
}
