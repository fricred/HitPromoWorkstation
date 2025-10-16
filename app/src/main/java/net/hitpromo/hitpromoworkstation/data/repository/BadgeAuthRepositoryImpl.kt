package net.hitpromo.hitpromoworkstation.data.repository

import android.util.Log
import net.hitpromo.hitpromoworkstation.data.model.BadgeLookupRequest
import net.hitpromo.hitpromoworkstation.data.model.BadgeLookupResponse
import net.hitpromo.hitpromoworkstation.data.remote.BadgeApiService
import net.hitpromo.hitpromoworkstation.domain.repository.BadgeAuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of BadgeAuthRepository using Retrofit API service.
 *
 * Handles badge authentication by calling the HR data badge scan lookup API.
 */
@Singleton
class BadgeAuthRepositoryImpl @Inject constructor(
    private val badgeApiService: BadgeApiService
) : BadgeAuthRepository {

    companion object {
        private const val TAG = "BadgeAuthRepository"
    }

    override suspend fun authenticateWithBadge(badgeId: String): Result<BadgeLookupResponse> {
        return try {
            Log.d(TAG, "Attempting badge authentication for: $badgeId")

            val request = BadgeLookupRequest(operatorId = badgeId)
            val response = badgeApiService.lookupBadge(request)

            if (response.success) {
                val operatorName = response.data?.name ?: response.operatorId
                Log.d(TAG, "Badge authentication successful: $operatorName")
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
