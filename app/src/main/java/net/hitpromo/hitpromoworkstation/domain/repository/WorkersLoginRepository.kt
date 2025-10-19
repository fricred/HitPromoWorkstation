package net.hitpromo.hitpromoworkstation.domain.repository

import net.hitpromo.hitpromoworkstation.data.remote.dto.WorkerLoginResponse

/**
 * Repository interface for worker login authentication.
 *
 * Provides methods to complete the second step of authentication by
 * submitting worker information to the workers API.
 */
interface WorkersLoginRepository {
    /**
     * Log in a worker using badge authentication data and machine ID.
     *
     * Requires a valid JWT token from badge authentication.
     * Makes a request to the workers login API to register the worker
     * with the specified machine and receive session credentials.
     *
     * @param machineId The machine ID where the worker is logging in
     * @param workerId The worker ID from badge lookup
     * @param workerName The worker name from badge lookup
     * @param workerEmail The worker email from badge lookup
     * @param jwtToken JWT token from badge authentication (Bearer token)
     * @return Result containing WorkerLoginResponse on success, or exception on failure
     */
    suspend fun loginWorker(
        machineId: String,
        workerId: String,
        workerName: String,
        workerEmail: String,
        jwtToken: String
    ): Result<WorkerLoginResponse>
}
