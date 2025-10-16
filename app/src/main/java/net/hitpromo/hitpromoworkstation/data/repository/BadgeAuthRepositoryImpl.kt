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
            Log.d(TAG, "Attempting badge authentication")

            val request = BadgeLookupRequest(operatorId = badgeId)
            val response = badgeApiService.lookupBadge(request)

            if (response.success) {
                Log.d(TAG, "Badge authentication successful")

                // Store JWT token if present in response
                if (!response.token.isNullOrEmpty()) {
                    try {
                        // Decode token to get expiration time
                        val claims = JwtTokenManager.decodeClaims(response.token)
                        if (claims != null && claims.exp != null) {
                            val expirationTimeMs = claims.exp * 1000L
                            userPreferences.saveJwtToken(response.token, expirationTimeMs)
                            Log.d(TAG, "JWT token stored successfully")
                        } else {
                            Log.e(TAG, "Invalid JWT token - cannot extract expiration claims")
                            // Fail authentication if token is invalid
                            return Result.failure(Exception("Invalid JWT token received from server: missing expiration"))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "JWT token validation failed", e)
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
                Result.failure(Exception(errorMessage))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Badge authentication error", e)
            Result.failure(e)
        }
    }
}
