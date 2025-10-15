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

    val name: String? = null,

    val department: String? = null,

    val role: String? = null,

    val message: String? = null
)
