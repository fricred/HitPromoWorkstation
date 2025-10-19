package net.hitpromo.hitpromoworkstation.data.repository

import android.util.Log
import net.hitpromo.hitpromoworkstation.data.local.UserPreferences
import net.hitpromo.hitpromoworkstation.data.remote.WorkersApiService
import net.hitpromo.hitpromoworkstation.data.remote.dto.WorkerLoginRequest
import net.hitpromo.hitpromoworkstation.data.remote.dto.WorkerLoginResponse
import net.hitpromo.hitpromoworkstation.domain.repository.WorkersLoginRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WorkersLoginRepository using Retrofit API service.
 *
 * Handles worker login by calling the workers API endpoint with
 * badge authentication data and machine ID, storing the returned session ID.
 */
@Singleton
class WorkersLoginRepositoryImpl @Inject constructor(
    private val workersApiService: WorkersApiService,
    private val userPreferences: UserPreferences
) : WorkersLoginRepository {

    companion object {
        private const val TAG = "WorkersLoginRepository"
    }

    override suspend fun loginWorker(
        machineId: String,
        workerId: String,
        workerName: String,
        workerEmail: String,
        jwtToken: String
    ): Result<WorkerLoginResponse> {
        return try {
            Log.d(TAG, "=== Starting Worker Login ===")
            Log.d(TAG, "Machine ID: $machineId")
            Log.d(TAG, "Worker ID: $workerId")
            Log.d(TAG, "Worker Name: $workerName")
            Log.d(TAG, "Worker Email: $workerEmail")
            Log.d(TAG, "JWT Token parameter passed: ${if (jwtToken.isNotEmpty()) "YES (${jwtToken.length} chars)" else "NO"}")

            val request = WorkerLoginRequest(
                machineId = machineId,
                workerId = workerId,
                workerName = workerName,
                workerEmail = workerEmail
            )

            Log.d(TAG, "Request body prepared: ${request.javaClass.simpleName}")

            // Token is automatically injected by TokenAuthInterceptor
            // No need to pass authorization header manually
            Log.d(TAG, "Making API call to workers endpoint...")
            Log.d(TAG, "TokenAuthInterceptor should intercept this request and add Authorization header")

            val response = workersApiService.loginWorker(request)

            Log.d(TAG, "Worker login API call completed successfully!")
            Log.d(TAG, "Response status: ${response.status}")
            Log.d(TAG, "Session ID received: ${response.sessionId}")
            Log.d(TAG, "Worker login successful")

            // Store session ID for future requests
            userPreferences.saveSessionId(response.sessionId)
            userPreferences.saveMachineId(machineId)

            Log.d(TAG, "Session and Machine IDs saved to preferences")
            Log.d(TAG, "=== Worker Login Complete ===")

            Result.success(response)

        } catch (e: Exception) {
            Log.e(TAG, "=== Worker Login Failed ===", e)
            Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Error message: ${e.message}")

            // Log more details about the exception
            val cause = e.cause
            if (cause != null) {
                Log.e(TAG, "Root cause: ${cause.javaClass.simpleName} - ${cause.message}")
            }

            Result.failure(e)
        }
    }
}
