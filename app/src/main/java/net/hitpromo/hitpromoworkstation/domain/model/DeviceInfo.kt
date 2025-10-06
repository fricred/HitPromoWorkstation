package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Represents device identification and status information.
 *
 * Contains metadata about the tablet device used for streaming,
 * useful for fleet management and troubleshooting.
 *
 * @property deviceId Unique identifier for this device
 * @property model Device model (e.g., "Samsung Galaxy Tab A9+")
 * @property osVersion Android OS version (e.g., "14")
 * @property appVersion Application version (e.g., "1.0.0")
 * @property status Current operational status
 * @property location Optional physical location identifier (e.g., "Production Line 3")
 * @property assignedUser Optional username of person using this device
 * @property lastSyncTime Last time device synced with backend (epoch millis)
 */
data class DeviceInfo(
    val deviceId: String,
    val model: String = "Unknown",
    val osVersion: String = "Unknown",
    val appVersion: String = "1.0.0",
    val status: DeviceStatus = DeviceStatus.ACTIVE,
    val location: String? = null,
    val assignedUser: String? = null,
    val lastSyncTime: Long? = null
) {
    /**
     * Device operational status.
     */
    enum class DeviceStatus {
        /** Device is active and operational */
        ACTIVE,

        /** Device is idle (not streaming) */
        IDLE,

        /** Device is in maintenance mode */
        MAINTENANCE,

        /** Device is offline or unreachable */
        OFFLINE,

        /** Device has encountered an error */
        ERROR;

        val displayName: String
            get() = when (this) {
                ACTIVE -> "Active"
                IDLE -> "Idle"
                MAINTENANCE -> "Maintenance"
                OFFLINE -> "Offline"
                ERROR -> "Error"
            }

        val isOperational: Boolean
            get() = this == ACTIVE || this == IDLE
    }

    /**
     * Check if device info is complete.
     */
    val isComplete: Boolean
        get() = deviceId.isNotBlank() &&
                model.isNotBlank() &&
                osVersion.isNotBlank()

    /**
     * Get formatted display name for the device.
     */
    val displayName: String
        get() = buildString {
            append(model)
            if (location != null) {
                append(" - ")
                append(location)
            }
        }

    /**
     * Get formatted last sync time.
     */
    val formattedLastSync: String?
        get() = lastSyncTime?.let { syncTime ->
            val elapsed = System.currentTimeMillis() - syncTime
            val seconds = elapsed / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            when {
                days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
                hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
                minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
                else -> "Just now"
            }
        }

    /**
     * Check if device needs sync (last sync > 5 minutes ago).
     */
    val needsSync: Boolean
        get() = lastSyncTime?.let { syncTime ->
            (System.currentTimeMillis() - syncTime) > 300_000 // 5 minutes
        } ?: true

    companion object {
        /**
         * Create a device info instance for the current device.
         * Actual implementation would retrieve real device information.
         */
        fun getCurrentDevice(): DeviceInfo {
            return DeviceInfo(
                deviceId = "TAB-A9P-001",
                model = "Samsung Galaxy Tab A9+ (SM-X210)",
                osVersion = "Android 14",
                appVersion = "1.0.0",
                status = DeviceStatus.IDLE,
                location = "Production Floor - Area 1",
                assignedUser = null,
                lastSyncTime = System.currentTimeMillis()
            )
        }

        /**
         * Example device for testing.
         */
        val Sample = DeviceInfo(
            deviceId = "TAB-A9P-002",
            model = "Samsung Galaxy Tab A9+",
            osVersion = "Android 14",
            appVersion = "1.0.0",
            status = DeviceStatus.ACTIVE,
            location = "Production Floor - Area 2",
            assignedUser = "operator_john",
            lastSyncTime = System.currentTimeMillis() - 60000 // 1 minute ago
        )
    }
}
