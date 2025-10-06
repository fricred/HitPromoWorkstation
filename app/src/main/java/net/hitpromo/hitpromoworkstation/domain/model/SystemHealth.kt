package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Represents real-time system health metrics for the tablet device.
 *
 * Monitors critical system resources to ensure stable streaming operation
 * and alert operators to potential issues before they impact production.
 *
 * @property networkStrength Network signal strength (0.0 = no signal, 1.0 = excellent)
 * @property networkType Type of network connection (WiFi, LTE, etc.)
 * @property cpuUsage CPU usage percentage (0.0 to 1.0)
 * @property memoryUsage Memory usage percentage (0.0 to 1.0)
 * @property memoryAvailableMb Available memory in megabytes
 * @property temperature Device temperature in Celsius
 * @property batteryLevel Battery level percentage (0.0 to 1.0)
 * @property isCharging Whether device is currently charging
 * @property storageAvailableGb Available storage space in gigabytes
 * @property uptimeSeconds Device uptime in seconds
 */
data class SystemHealth(
    val networkStrength: Float = 1.0f,
    val networkType: NetworkType = NetworkType.WIFI,
    val cpuUsage: Float = 0.3f,
    val memoryUsage: Float = 0.5f,
    val memoryAvailableMb: Int = 2048,
    val temperature: Float = 35.0f,
    val batteryLevel: Float = 0.8f,
    val isCharging: Boolean = true,
    val storageAvailableGb: Float = 10.5f,
    val uptimeSeconds: Long = 3600
) {
    /**
     * Network connection types.
     */
    enum class NetworkType {
        WIFI,
        ETHERNET,
        LTE,
        MOBILE_5G,
        MOBILE_4G,
        MOBILE_3G,
        UNKNOWN;

        val displayName: String
            get() = when (this) {
                WIFI -> "Wi-Fi"
                ETHERNET -> "Ethernet"
                LTE -> "LTE"
                MOBILE_5G -> "5G"
                MOBILE_4G -> "4G"
                MOBILE_3G -> "3G"
                UNKNOWN -> "Unknown"
            }

        val isReliable: Boolean
            get() = this == WIFI || this == ETHERNET || this == MOBILE_5G
    }

    /**
     * Overall system health status.
     */
    enum class HealthStatus {
        EXCELLENT,
        GOOD,
        WARNING,
        CRITICAL;

        val displayName: String
            get() = when (this) {
                EXCELLENT -> "Excellent"
                GOOD -> "Good"
                WARNING -> "Warning"
                CRITICAL -> "Critical"
            }
    }

    /**
     * Check if system is healthy for streaming.
     * Considers network, CPU, memory, and temperature.
     */
    val isHealthy: Boolean
        get() = networkStrength > 0.5f &&
                cpuUsage < 0.8f &&
                memoryUsage < 0.85f &&
                temperature < 45.0f &&
                (batteryLevel > 0.2f || isCharging)

    /**
     * Get overall health status assessment.
     */
    val healthStatus: HealthStatus
        get() = when {
            !isHealthy -> HealthStatus.CRITICAL
            networkStrength < 0.7f || cpuUsage > 0.6f || temperature > 40.0f -> HealthStatus.WARNING
            networkStrength > 0.9f && cpuUsage < 0.4f && temperature < 38.0f -> HealthStatus.EXCELLENT
            else -> HealthStatus.GOOD
        }

    /**
     * Get list of active warnings/issues.
     */
    val warnings: List<String>
        get() = buildList {
            if (networkStrength < 0.5f) add("Weak network signal")
            if (!networkType.isReliable) add("Unstable network connection")
            if (cpuUsage > 0.8f) add("High CPU usage")
            if (memoryUsage > 0.85f) add("Low memory available")
            if (temperature > 45.0f) add("Device overheating")
            if (batteryLevel < 0.2f && !isCharging) add("Low battery")
            if (storageAvailableGb < 1.0f) add("Low storage space")
        }

    /**
     * Format uptime as human-readable string.
     */
    val formattedUptime: String
        get() {
            val hours = uptimeSeconds / 3600
            val minutes = (uptimeSeconds % 3600) / 60
            val seconds = uptimeSeconds % 60
            return when {
                hours > 0 -> "%dh %02dm".format(hours, minutes)
                minutes > 0 -> "%dm %02ds".format(minutes, seconds)
                else -> "%ds".format(seconds)
            }
        }

    /**
     * Format temperature with unit.
     */
    val formattedTemperature: String
        get() = "%.1f°C".format(temperature)

    /**
     * Format battery level as percentage.
     */
    val formattedBatteryLevel: String
        get() = "%d%%".format((batteryLevel * 100).toInt())

    /**
     * Format network strength as percentage.
     */
    val formattedNetworkStrength: String
        get() = "%d%%".format((networkStrength * 100).toInt())

    companion object {
        /**
         * Default healthy system state.
         */
        val Default = SystemHealth()

        /**
         * Example of a system under stress.
         */
        val HighLoad = SystemHealth(
            networkStrength = 0.6f,
            networkType = NetworkType.WIFI,
            cpuUsage = 0.75f,
            memoryUsage = 0.8f,
            memoryAvailableMb = 512,
            temperature = 42.0f,
            batteryLevel = 0.4f,
            isCharging = false,
            storageAvailableGb = 5.2f,
            uptimeSeconds = 28800
        )

        /**
         * Example of a critical system state.
         */
        val Critical = SystemHealth(
            networkStrength = 0.3f,
            networkType = NetworkType.MOBILE_3G,
            cpuUsage = 0.95f,
            memoryUsage = 0.92f,
            memoryAvailableMb = 256,
            temperature = 48.0f,
            batteryLevel = 0.1f,
            isCharging = false,
            storageAvailableGb = 0.5f,
            uptimeSeconds = 86400
        )
    }
}
