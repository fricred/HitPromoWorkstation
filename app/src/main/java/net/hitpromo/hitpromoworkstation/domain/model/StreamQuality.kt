package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Represents real-time stream quality metrics.
 *
 * Contains all relevant metrics for monitoring video stream health,
 * performance, and network conditions during active streaming.
 *
 * @property resolution Current video resolution (e.g., "1280x720")
 * @property fps Current frames per second being captured/streamed
 * @property bitrate Current bitrate in bits per second
 * @property latencyMs Current end-to-end latency in milliseconds
 * @property droppedFrames Number of frames dropped since stream started
 * @property networkQuality Subjective network quality assessment (0.0 = poor, 1.0 = excellent)
 */
data class StreamQuality(
    val resolution: String = "1280x720",
    val fps: Int = 30,
    val bitrate: Int = 2_500_000,
    val latencyMs: Int = 200,
    val droppedFrames: Int = 0,
    val networkQuality: Float = 1.0f
) {
    /**
     * Check if quality meets production requirements.
     * Considers FPS, latency, and network quality.
     */
    val isHealthy: Boolean
        get() = fps >= 15 &&
                latencyMs < 1000 &&
                networkQuality > 0.5f

    /**
     * Get quality rating as a percentage (0-100).
     * Composite score based on FPS stability, latency, and network quality.
     */
    val qualityPercentage: Int
        get() {
            val fpsScore = (fps.toFloat() / 30f).coerceIn(0f, 1f)
            val latencyScore = (1f - (latencyMs.toFloat() / 2000f)).coerceIn(0f, 1f)
            val compositeScore = (fpsScore * 0.4f + latencyScore * 0.3f + networkQuality * 0.3f)
            return (compositeScore * 100).toInt()
        }

    /**
     * Get human-readable quality description.
     */
    val qualityDescription: String
        get() = when {
            qualityPercentage >= 80 -> "Excellent"
            qualityPercentage >= 60 -> "Good"
            qualityPercentage >= 40 -> "Fair"
            qualityPercentage >= 20 -> "Poor"
            else -> "Critical"
        }

    /**
     * Format bitrate for display (e.g., "2.5 Mbps").
     */
    val formattedBitrate: String
        get() {
            val mbps = bitrate / 1_000_000.0
            return "%.1f Mbps".format(mbps)
        }

    companion object {
        /**
         * Default quality settings for initialization.
         */
        val Default = StreamQuality()

        /**
         * High quality settings (1080p at 30fps).
         */
        val High = StreamQuality(
            resolution = "1920x1080",
            fps = 30,
            bitrate = 4_000_000,
            latencyMs = 150,
            droppedFrames = 0,
            networkQuality = 1.0f
        )

        /**
         * Medium quality settings (720p at 30fps).
         */
        val Medium = StreamQuality(
            resolution = "1280x720",
            fps = 30,
            bitrate = 2_500_000,
            latencyMs = 200,
            droppedFrames = 0,
            networkQuality = 0.9f
        )

        /**
         * Low quality settings (720p at 15fps) for poor network conditions.
         */
        val Low = StreamQuality(
            resolution = "1280x720",
            fps = 15,
            bitrate = 1_000_000,
            latencyMs = 400,
            droppedFrames = 5,
            networkQuality = 0.6f
        )
    }
}
