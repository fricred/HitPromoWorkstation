package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Represents camera hardware settings and configuration.
 *
 * Contains settings that control camera behavior during video streaming,
 * optimized for industrial production floor environments.
 *
 * @property autoFocus Whether auto-focus is enabled
 * @property flashEnabled Whether camera flash is enabled (for low-light conditions)
 * @property resolution Capture resolution in "WIDTHxHEIGHT" format
 * @property targetFps Target frames per second for capture
 * @property exposureCompensation Exposure compensation value (-2.0 to 2.0)
 * @property focusMode Camera focus mode
 * @property whiteBalanceMode White balance mode for color correction
 */
data class CameraSettings(
    val autoFocus: Boolean = true,
    val flashEnabled: Boolean = false,
    val resolution: String = "1280x720",
    val targetFps: Int = 30,
    val exposureCompensation: Float = 0.0f,
    val focusMode: FocusMode = FocusMode.CONTINUOUS_VIDEO,
    val whiteBalanceMode: WhiteBalanceMode = WhiteBalanceMode.AUTO
) {
    /**
     * Camera focus modes.
     */
    enum class FocusMode {
        /** Fixed focus at infinity */
        FIXED,

        /** Auto-focus triggered once */
        AUTO,

        /** Continuous auto-focus optimized for still images */
        CONTINUOUS_PICTURE,

        /** Continuous auto-focus optimized for video */
        CONTINUOUS_VIDEO,

        /** Manual focus control */
        MANUAL;

        val displayName: String
            get() = when (this) {
                FIXED -> "Fixed"
                AUTO -> "Auto"
                CONTINUOUS_PICTURE -> "Continuous Picture"
                CONTINUOUS_VIDEO -> "Continuous Video"
                MANUAL -> "Manual"
            }
    }

    /**
     * White balance modes for color correction.
     */
    enum class WhiteBalanceMode {
        /** Automatic white balance */
        AUTO,

        /** Incandescent lighting */
        INCANDESCENT,

        /** Fluorescent lighting */
        FLUORESCENT,

        /** Daylight */
        DAYLIGHT,

        /** Cloudy daylight */
        CLOUDY,

        /** Manual white balance */
        MANUAL;

        val displayName: String
            get() = when (this) {
                AUTO -> "Auto"
                INCANDESCENT -> "Incandescent"
                FLUORESCENT -> "Fluorescent"
                DAYLIGHT -> "Daylight"
                CLOUDY -> "Cloudy"
                MANUAL -> "Manual"
            }
    }

    /**
     * Validate settings are within acceptable ranges.
     */
    val isValid: Boolean
        get() = targetFps in 10..60 &&
                exposureCompensation in -2.0f..2.0f &&
                resolution.matches(Regex("\\d+x\\d+"))

    /**
     * Get resolution as width and height pair.
     */
    val resolutionPair: Pair<Int, Int>
        get() {
            val parts = resolution.split("x")
            return if (parts.size == 2) {
                parts[0].toIntOrNull() to parts[1].toIntOrNull()
            } else {
                1280 to 720
            }.let { (width, height) ->
                (width ?: 1280) to (height ?: 720)
            }
        }

    companion object {
        /**
         * Default settings optimized for industrial environments.
         */
        val Default = CameraSettings()

        /**
         * High quality settings for optimal video capture.
         */
        val HighQuality = CameraSettings(
            autoFocus = true,
            flashEnabled = false,
            resolution = "1920x1080",
            targetFps = 30,
            exposureCompensation = 0.0f,
            focusMode = FocusMode.CONTINUOUS_VIDEO,
            whiteBalanceMode = WhiteBalanceMode.AUTO
        )

        /**
         * Low light settings with flash enabled.
         */
        val LowLight = CameraSettings(
            autoFocus = true,
            flashEnabled = true,
            resolution = "1280x720",
            targetFps = 30,
            exposureCompensation = 1.0f,
            focusMode = FocusMode.CONTINUOUS_VIDEO,
            whiteBalanceMode = WhiteBalanceMode.INCANDESCENT
        )

        /**
         * Power-saving settings with reduced resolution and frame rate.
         */
        val PowerSaving = CameraSettings(
            autoFocus = true,
            flashEnabled = false,
            resolution = "1280x720",
            targetFps = 15,
            exposureCompensation = 0.0f,
            focusMode = FocusMode.CONTINUOUS_VIDEO,
            whiteBalanceMode = WhiteBalanceMode.AUTO
        )
    }
}
