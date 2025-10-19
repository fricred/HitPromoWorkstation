package net.hitpromo.hitpromoworkstation.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request DTO for worker login endpoint.
 *
 * Submits worker information along with JWT token from badge authentication
 * to complete the second step of the authentication flow.
 *
 * Required fields:
 * - machine_id: Machine/tablet identifier
 * - worker_id: Operator/worker identifier from badge
 * - worker_name: Full name of the worker
 * - worker_email: Email address of the worker
 */
@JsonClass(generateAdapter = true)
data class WorkerLoginRequest(
    @Json(name = "machine_id")
    val machineId: String,

    @Json(name = "worker_id")
    val workerId: String,

    @Json(name = "worker_name")
    val workerName: String,

    @Json(name = "worker_email")
    val workerEmail: String
)
