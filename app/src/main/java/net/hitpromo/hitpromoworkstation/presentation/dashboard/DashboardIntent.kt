package net.hitpromo.hitpromoworkstation.presentation.dashboard

import net.hitpromo.hitpromoworkstation.domain.model.CameraSettings
import net.hitpromo.hitpromoworkstation.domain.model.StreamProtocol

/**
 * MVI Intent sealed class representing all possible user actions on the dashboard screen.
 *
 * Following the established MVI pattern from LoginIntent, this class models
 * all user interactions with the streaming control dashboard.
 */
sealed class DashboardIntent {

    // ========================================
    // Streaming Control Actions
    // ========================================

    /**
     * User wants to start streaming with a specific protocol.
     *
     * @property protocol Streaming protocol to use (WebRTC or RTSP)
     */
    data class StartStream(val protocol: StreamProtocol) : DashboardIntent()

    /**
     * User wants to stop the active stream.
     */
    data object StopStream : DashboardIntent()

    /**
     * User wants to restart the stream with current settings.
     */
    data object RestartStream : DashboardIntent()

    /**
     * User wants to switch the streaming protocol.
     * This will restart the stream with the new protocol.
     *
     * @property protocol New protocol to switch to
     */
    data class SwitchProtocol(val protocol: StreamProtocol) : DashboardIntent()

    // ========================================
    // Camera Control Actions
    // ========================================

    /**
     * User wants to update camera settings.
     *
     * @property settings New camera settings to apply
     */
    data class UpdateCameraSettings(val settings: CameraSettings) : DashboardIntent()

    /**
     * User wants to switch between available cameras.
     *
     * @property cameraId Camera identifier to switch to
     */
    data class SwitchCamera(val cameraId: String) : DashboardIntent()

    /**
     * User wants to toggle camera flash.
     */
    data object ToggleFlash : DashboardIntent()

    /**
     * User wants to toggle auto-focus.
     */
    data object ToggleAutoFocus : DashboardIntent()

    // ========================================
    // Analytics Actions
    // ========================================

    /**
     * User wants to reset analytics counters.
     */
    data object ResetAnalytics : DashboardIntent()

    /**
     * User wants to refresh analytics data.
     */
    data object RefreshAnalytics : DashboardIntent()

    /**
     * User wants to view detailed analytics for a specific object type.
     *
     * @property objectType Object type to view details for
     */
    data class ViewObjectDetails(val objectType: String) : DashboardIntent()

    // ========================================
    // System Monitoring Actions
    // ========================================

    /**
     * User wants to refresh system health data.
     */
    data object RefreshSystemHealth : DashboardIntent()

    /**
     * User wants to update device location.
     *
     * @property location New physical location identifier
     */
    data class UpdateDeviceLocation(val location: String) : DashboardIntent()

    /**
     * User wants to sync device information with backend.
     */
    data object SyncDeviceInfo : DashboardIntent()

    /**
     * User wants to view system warnings/issues.
     */
    data object ViewSystemWarnings : DashboardIntent()

    // ========================================
    // UI State Management Actions
    // ========================================

    /**
     * User wants to clear any error messages.
     */
    data object ClearError : DashboardIntent()

    /**
     * User wants to dismiss a notification or alert.
     *
     * @property notificationId Identifier of notification to dismiss
     */
    data class DismissNotification(val notificationId: String) : DashboardIntent()

    /**
     * User wants to toggle expanded/collapsed state of a dashboard section.
     *
     * @property section Section identifier to toggle
     */
    data class ToggleSection(val section: DashboardSection) : DashboardIntent()

    /**
     * User wants to refresh all dashboard data.
     */
    data object RefreshAll : DashboardIntent()

    /**
     * User wants to sign out and return to login.
     */
    data object SignOut : DashboardIntent()

    // ========================================
    // Settings Actions
    // ========================================

    /**
     * User wants to open settings screen.
     */
    data object OpenSettings : DashboardIntent()

    /**
     * User wants to view help/documentation.
     */
    data object OpenHelp : DashboardIntent()

    /**
     * User wants to view about/device info screen.
     */
    data object OpenAbout : DashboardIntent()
}

/**
 * Dashboard sections that can be expanded/collapsed.
 */
enum class DashboardSection {
    STREAM_CONTROLS,
    CAMERA_SETTINGS,
    ANALYTICS,
    SYSTEM_HEALTH,
    DEVICE_INFO
}
