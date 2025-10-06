package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Represents real-time analytics summary from object detection processing.
 *
 * Contains aggregated metrics about detected objects and events during
 * the current streaming session, updated in real-time from AWS analytics.
 *
 * @property totalDetections Total number of objects detected since stream started
 * @property objectCounts Map of object types to their detection counts
 * @property averageConfidence Average confidence score across all detections (0.0 to 1.0)
 * @property detectionRate Detections per minute
 * @property lastDetectionTime Timestamp of the most recent detection (epoch millis)
 * @property sessionStartTime Timestamp when analytics session started (epoch millis)
 * @property alerts Number of alert-worthy detections (high-priority objects or events)
 */
data class AnalyticsSummary(
    val totalDetections: Int = 0,
    val objectCounts: Map<String, Int> = emptyMap(),
    val averageConfidence: Float = 0.0f,
    val detectionRate: Float = 0.0f,
    val lastDetectionTime: Long? = null,
    val sessionStartTime: Long = System.currentTimeMillis(),
    val alerts: Int = 0
) {
    /**
     * Common object types for industrial environments.
     */
    object ObjectTypes {
        const val PERSON = "person"
        const val FORKLIFT = "forklift"
        const val PALLET = "pallet"
        const val BOX = "box"
        const val VEHICLE = "vehicle"
        const val HAZARD = "hazard"
        const val TOOL = "tool"
        const val EQUIPMENT = "equipment"
    }

    /**
     * Check if analytics data is available.
     */
    val hasData: Boolean
        get() = totalDetections > 0

    /**
     * Check if analytics system is actively detecting.
     */
    val isActive: Boolean
        get() = lastDetectionTime != null &&
                (System.currentTimeMillis() - lastDetectionTime) < 10000 // Within last 10 seconds

    /**
     * Get session duration in seconds.
     */
    val sessionDurationSeconds: Long
        get() = (System.currentTimeMillis() - sessionStartTime) / 1000

    /**
     * Get formatted session duration.
     */
    val formattedSessionDuration: String
        get() {
            val hours = sessionDurationSeconds / 3600
            val minutes = (sessionDurationSeconds % 3600) / 60
            val seconds = sessionDurationSeconds % 60
            return when {
                hours > 0 -> "%dh %02dm".format(hours, minutes)
                minutes > 0 -> "%dm %02ds".format(minutes, seconds)
                else -> "%ds".format(seconds)
            }
        }

    /**
     * Get top detected objects sorted by count.
     */
    val topDetectedObjects: List<Pair<String, Int>>
        get() = objectCounts.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key to it.value }

    /**
     * Get count for a specific object type.
     */
    fun getCountForObject(objectType: String): Int =
        objectCounts[objectType] ?: 0

    /**
     * Check if a specific object type has been detected.
     */
    fun hasDetected(objectType: String): Boolean =
        getCountForObject(objectType) > 0

    /**
     * Get percentage of total detections for an object type.
     */
    fun getPercentageForObject(objectType: String): Float {
        if (totalDetections == 0) return 0f
        val count = getCountForObject(objectType)
        return (count.toFloat() / totalDetections.toFloat()) * 100f
    }

    /**
     * Format confidence as percentage.
     */
    val formattedConfidence: String
        get() = "%.1f%%".format(averageConfidence * 100)

    /**
     * Format detection rate.
     */
    val formattedDetectionRate: String
        get() = "%.1f/min".format(detectionRate)

    companion object {
        /**
         * Empty analytics state.
         */
        val Empty = AnalyticsSummary()

        /**
         * Example analytics with sample data for UI development.
         */
        val Sample = AnalyticsSummary(
            totalDetections = 1247,
            objectCounts = mapOf(
                ObjectTypes.PERSON to 432,
                ObjectTypes.FORKLIFT to 45,
                ObjectTypes.PALLET to 568,
                ObjectTypes.BOX to 189,
                ObjectTypes.VEHICLE to 13
            ),
            averageConfidence = 0.87f,
            detectionRate = 24.5f,
            lastDetectionTime = System.currentTimeMillis() - 2000,
            sessionStartTime = System.currentTimeMillis() - 3600000, // 1 hour ago
            alerts = 3
        )

        /**
         * High activity analytics example.
         */
        val HighActivity = AnalyticsSummary(
            totalDetections = 5420,
            objectCounts = mapOf(
                ObjectTypes.PERSON to 2156,
                ObjectTypes.FORKLIFT to 234,
                ObjectTypes.PALLET to 2034,
                ObjectTypes.BOX to 892,
                ObjectTypes.VEHICLE to 104
            ),
            averageConfidence = 0.92f,
            detectionRate = 67.8f,
            lastDetectionTime = System.currentTimeMillis() - 500,
            sessionStartTime = System.currentTimeMillis() - 7200000, // 2 hours ago
            alerts = 18
        )
    }
}
