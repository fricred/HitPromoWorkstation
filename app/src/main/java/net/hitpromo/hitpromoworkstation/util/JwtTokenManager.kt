package net.hitpromo.hitpromoworkstation.util

import android.util.Log
import java.util.Base64
import kotlin.math.floor

/**
 * Manages JWT token parsing and validation.
 *
 * Provides utilities to decode JWT tokens, extract claims (expiration, user info),
 * and check token validity without requiring external cryptography libraries
 * (signature verification is skipped as the token comes from a trusted API).
 */
object JwtTokenManager {

    private const val TAG = "JwtTokenManager"
    private const val PARTS_COUNT = 3
    private const val BEARER_PREFIX = "Bearer "
    private const val EXPIRATION_BUFFER_SECONDS = 60L

    /**
     * Represents the payload of a JWT token.
     *
     * @property sub Subject (user identifier)
     * @property workerId Worker/operator ID
     * @property name User's full name
     * @property isSupervisor Whether user is a supervisor (0 = false, 1 = true)
     * @property exp Expiration timestamp in seconds since epoch
     * @property iat Issued at timestamp in seconds since epoch
     * @property jti JWT ID (unique identifier)
     */
    data class JwtClaims(
        val sub: String? = null,
        val workerId: String? = null,
        val name: String? = null,
        val isSupervisor: Boolean = false,
        val exp: Long? = null,
        val iat: Long? = null,
        val jti: String? = null,
        val rawClaims: Map<String, Any?> = emptyMap()
    )

    /**
     * Strips "Bearer " prefix from token if present.
     */
    fun stripBearerPrefix(token: String): String {
        return if (token.startsWith(BEARER_PREFIX, ignoreCase = true)) {
            token.substring(BEARER_PREFIX.length)
        } else {
            token
        }
    }

    /**
     * Decodes a JWT token and extracts its claims.
     *
     * @param token JWT token (with or without "Bearer " prefix)
     * @return JwtClaims if successful, null if token is invalid
     */
    fun decodeClaims(token: String): JwtClaims? {
        return try {
            val cleanToken = stripBearerPrefix(token)
            val parts = cleanToken.split(".")

            if (parts.size != PARTS_COUNT) {
                Log.w(TAG, "Invalid token format: expected 3 parts, got ${parts.size}")
                return null
            }

            val payloadJson = decodeBase64(parts[1]) ?: run {
                Log.w(TAG, "Failed to decode token payload")
                return null
            }

            val claims = parseJsonToClaims(payloadJson)
            Log.d(TAG, "Token decoded successfully for user: ${claims.workerId}")
            claims

        } catch (e: Exception) {
            Log.e(TAG, "Failed to decode JWT token", e)
            null
        }
    }

    /**
     * Checks if a JWT token is currently valid (not expired).
     *
     * Adds a 60-second buffer to account for clock skew and processing time.
     * Returns false if token cannot be decoded or is invalid.
     *
     * @param token JWT token to validate
     * @return True if token is valid and not expired, false otherwise
     */
    fun isTokenValid(token: String): Boolean {
        return try {
            val claims = decodeClaims(token) ?: return false
            val exp = claims.exp ?: return false

            val currentTimeSeconds = System.currentTimeMillis() / 1000L
            val expirationTime = exp - EXPIRATION_BUFFER_SECONDS

            currentTimeSeconds < expirationTime
        } catch (e: Exception) {
            Log.e(TAG, "Error validating token", e)
            false
        }
    }

    /**
     * Calculates time remaining until token expiration.
     *
     * @param token JWT token
     * @return Remaining time in seconds, or null if token is invalid or has no expiration
     */
    fun getTimeUntilExpiration(token: String): Long? {
        return try {
            val claims = decodeClaims(token) ?: return null
            val exp = claims.exp ?: return null

            val currentTimeSeconds = System.currentTimeMillis() / 1000L
            val remaining = exp - currentTimeSeconds

            if (remaining > 0) remaining else null
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating expiration time", e)
            null
        }
    }

    /**
     * Gets the formatted expiration time of a token.
     *
     * @param token JWT token
     * @return Formatted expiration time string, or null if invalid
     */
    fun getExpirationTimeString(token: String): String? {
        return try {
            val claims = decodeClaims(token) ?: return null
            val exp = claims.exp ?: return null

            val expirationMs = exp * 1000L
            java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.US
            ).format(java.util.Date(expirationMs))
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting expiration time", e)
            null
        }
    }

    /**
     * Decodes a base64URL-encoded string.
     *
     * Handles base64URL padding variations that may occur in JWT tokens.
     *
     * @param encodedString Base64URL-encoded string
     * @return Decoded string, or null if decoding fails
     */
    private fun decodeBase64(encodedString: String): String? {
        return try {
            // Add padding if needed (base64URL may omit padding)
            val padded = when (encodedString.length % 4) {
                2 -> "$encodedString=="
                3 -> "$encodedString="
                else -> encodedString
            }

            val decodedBytes = Base64.getUrlDecoder().decode(padded)
            String(decodedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Base64 decoding failed", e)
            null
        }
    }

    /**
     * Parses JSON payload into JwtClaims.
     *
     * Handles both standard JWT claims and custom claims from the API.
     * Gracefully handles missing or malformed claims.
     *
     * @param jsonString JSON string from JWT payload
     * @return Parsed JwtClaims
     */
    private fun parseJsonToClaims(jsonString: String): JwtClaims {
        return try {
            // Simple JSON parsing without external dependencies
            // This is a lightweight implementation for known JWT structure
            val claimsMap = mutableMapOf<String, Any?>()

            // Extract standard claims using regex patterns
            claimsMap["sub"] = extractJsonStringValue(jsonString, "sub")
            claimsMap["worker_id"] = extractJsonStringValue(jsonString, "worker_id")
            claimsMap["name"] = extractJsonStringValue(jsonString, "name")
            claimsMap["jti"] = extractJsonStringValue(jsonString, "jti")

            val isSupervisor = extractJsonIntValue(jsonString, "is_supervisor")
            val exp = extractJsonLongValue(jsonString, "exp")
            val iat = extractJsonLongValue(jsonString, "iat")

            JwtClaims(
                sub = claimsMap["sub"] as? String,
                workerId = claimsMap["worker_id"] as? String,
                name = claimsMap["name"] as? String,
                isSupervisor = isSupervisor == 1,
                exp = exp,
                iat = iat,
                jti = claimsMap["jti"] as? String,
                rawClaims = claimsMap
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JWT claims", e)
            JwtClaims()
        }
    }

    /**
     * Extracts a string value from JSON without external dependencies.
     */
    private fun extractJsonStringValue(json: String, key: String): String? {
        return try {
            val regex = """"$key"\s*:\s*"([^"]*)"""".toRegex()
            regex.find(json)?.groupValues?.getOrNull(1)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extracts an integer value from JSON without external dependencies.
     */
    private fun extractJsonIntValue(json: String, key: String): Int? {
        return try {
            val regex = """"$key"\s*:\s*(\d+)""".toRegex()
            regex.find(json)?.groupValues?.getOrNull(1)?.toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extracts a long value from JSON without external dependencies.
     */
    private fun extractJsonLongValue(json: String, key: String): Long? {
        return try {
            val regex = """"$key"\s*:\s*(\d+)""".toRegex()
            regex.find(json)?.groupValues?.getOrNull(1)?.toLongOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
