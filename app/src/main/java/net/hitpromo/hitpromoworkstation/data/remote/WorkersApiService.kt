package net.hitpromo.hitpromoworkstation.data.remote

import net.hitpromo.hitpromoworkstation.data.remote.dto.WorkerLoginRequest
import net.hitpromo.hitpromoworkstation.data.remote.dto.WorkerLoginResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit API service for workers authentication endpoints.
 *
 * Provides methods to complete the second step of authentication by
 * submitting worker information and receiving session credentials.
 */
interface WorkersApiService {
    /**
     * Log in worker after badge authentication.
     *
     * Requires JWT Bearer token from badge authentication in Authorization header.
     * The token is automatically injected by TokenAuthInterceptor.
     * Returns 202 Accepted with session information.
     *
     * @param request Worker login request with machine ID and operator data
     * @return Worker login response with session_id and status
     */
    @POST("api/workers/login")
    suspend fun loginWorker(
        @Body request: WorkerLoginRequest
    ): WorkerLoginResponse
}
