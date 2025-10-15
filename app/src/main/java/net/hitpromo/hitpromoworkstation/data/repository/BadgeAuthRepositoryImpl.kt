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

        // TEMPORARY: Fixed bearer token for initial testing
        // TODO: User will provide real token tomorrow
        private const val BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzY3NzMxODk1LCJpYXQiOjE3NTk5NTU4OTUsImp0aSI6IjBjNDQ4NjJmODI4NjRlZDZiNWRlMWQxNDQzNjdmMjJjIiwidXNlcl9pZCI6M30.ad-_VRs_C2nGKSUPMEW3RRQr-qLTe62ZRDqd4OGszv4"
    }

    override suspend fun authenticateWithBadge(badgeId: String): Result<BadgeLookupResponse> {
        return try {
            Log.d(TAG, "Attempting badge authentication for: $badgeId")

            val request = BadgeLookupRequest(operatorId = badgeId)
            val response = badgeApiService.lookupBadge(BEARER_TOKEN, request)

            if (response.success) {
                Log.d(TAG, "Badge authentication successful: ${response.name}")
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
