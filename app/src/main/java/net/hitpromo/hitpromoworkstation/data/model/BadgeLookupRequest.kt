package net.hitpromo.hitpromoworkstation.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request DTO for badge lookup API.
 *
 * Used to authenticate a user by their employee badge ID scanned
 * from a barcode scanner.
 */
@JsonClass(generateAdapter = true)
data class BadgeLookupRequest(
    @Json(name = "operator_id")
    val operatorId: String
)
