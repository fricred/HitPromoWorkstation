package net.hitpromo.hitpromoworkstation.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response DTO for worker login endpoint (202 Accepted).
 *
 * Contains session information and provisioning status after successful
 * worker authentication and machine registration.
 */
@JsonClass(generateAdapter = true)
data class WorkerLoginResponse(
    @Json(name = "session_id")
    val sessionId: String,

    @Json(name = "machine_id")
    val machineId: String,

    @Json(name = "worker_id")
    val workerId: String,

    val status: String,

    val message: String? = null,

    @Json(name = "task_arn")
    val taskArn: String? = null,

    @Json(name = "estimated_ready_time")
    val estimatedReadyTime: String? = null
)
