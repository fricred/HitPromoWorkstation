package net.hitpromo.hitpromoworkstation.data.remote

import net.hitpromo.hitpromoworkstation.data.model.BadgeLookupRequest
import net.hitpromo.hitpromoworkstation.data.model.BadgeLookupResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit API service for badge authentication endpoints.
 *
 * Provides methods to authenticate users via their employee badge IDs
 * by calling the HR data badge scan lookup API.
 */
interface BadgeApiService {
    /**
     * Look up operator information by badge ID.
     *
     * @param bearerToken Bearer token for API authentication
     * @param request Badge lookup request containing operator_id
     * @return Badge lookup response with operator details
     */
    @POST("api/hrdata/badgescan/lookup/")
    suspend fun lookupBadge(
        @Header("Authorization") bearerToken: String,
        @Body request: BadgeLookupRequest
    ): BadgeLookupResponse
}
