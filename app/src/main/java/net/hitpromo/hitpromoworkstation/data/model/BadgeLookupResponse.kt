package net.hitpromo.hitpromoworkstation.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response DTO for badge lookup API.
 *
 * Contains operator information retrieved from the HR system
 * based on the scanned badge ID, along with a JWT token for
 * subsequent API requests. The token is valid for 24 hours.
 */
@JsonClass(generateAdapter = true)
data class BadgeLookupResponse(
    val success: Boolean,

    @Json(name = "operator_id")
    val operatorId: String,

    val data: OperatorData? = null,

    val message: String? = null,

    /**
     * JWT token for authenticating subsequent API requests.
     * Valid for 24 hours from issuance. Should be included in the
     * Authorization header as "Bearer <token>".
     */
    val token: String? = null
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
