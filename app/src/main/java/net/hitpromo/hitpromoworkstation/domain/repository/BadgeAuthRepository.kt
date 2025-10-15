package net.hitpromo.hitpromoworkstation.domain.repository

import net.hitpromo.hitpromoworkstation.data.model.BadgeLookupResponse

/**
 * Repository interface for badge-based authentication.
 *
 * Provides methods to authenticate users via their employee badge IDs
 * scanned from barcode scanners.
 */
interface BadgeAuthRepository {
    /**
     * Authenticate a user by their badge ID.
     *
     * Makes a request to the badge lookup API to validate the badge
     * and retrieve operator information.
     *
     * @param badgeId The employee badge ID (operator ID) from barcode scan
     * @return Result containing BadgeLookupResponse on success, or exception on failure
     */
    suspend fun authenticateWithBadge(badgeId: String): Result<BadgeLookupResponse>
}
