package net.hitpromo.hitpromoworkstation.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response DTO for badge lookup API.
 *
 * Contains operator information retrieved from the HR system
 * based on the scanned badge ID.
 */
@JsonClass(generateAdapter = true)
data class BadgeLookupResponse(
    val success: Boolean,

    @Json(name = "operator_id")
    val operatorId: String,

    val data: OperatorData? = null,

    val message: String? = null
)

/**
 * Operator data nested object from badge lookup response.
 */
@JsonClass(generateAdapter = true)
data class OperatorData(
    val name: String,

    @Json(name = "first_name")
    val firstName: String,

    @Json(name = "last_name")
    val lastName: String,

    @Json(name = "is_supervisor")
    val isSupervisor: Int
)
