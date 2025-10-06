package net.hitpromo.hitpromoworkstation.presentation.dashboard

import net.hitpromo.hitpromoworkstation.domain.model.AnalyticsSummary
import net.hitpromo.hitpromoworkstation.domain.model.CameraSettings
import net.hitpromo.hitpromoworkstation.domain.model.DeviceInfo
import net.hitpromo.hitpromoworkstation.domain.model.StreamProtocol
import net.hitpromo.hitpromoworkstation.domain.model.StreamQuality
import net.hitpromo.hitpromoworkstation.domain.model.StreamState
import net.hitpromo.hitpromoworkstation.domain.model.SystemHealth
import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.model.canStart
import net.hitpromo.hitpromoworkstation.domain.model.canStop
import net.hitpromo.hitpromoworkstation.domain.model.isActive

/**
 * UI State for the dashboard screen using MVI pattern.
 *
 * Following the established pattern from LoginUiState, this data class
 * contains all state needed to render the streaming control dashboard.
 *
 * @property user Currently authenticated user
 * @property streamState Current state of the video stream
 * @property streamQuality Real-time stream quality metrics (null when not streaming)
 * @property cameraSettings Current camera configuration
 * @property systemHealth Real-time system health metrics
 * @property analyticsSummary Real-time analytics data
 * @property deviceInfo Device metadata and status
 * @property isLoading Whether a background operation is in progress
 * @property errorMessage Optional error message to display
 * @property successMessage Optional success message to display
 * @property expandedSections Set of currently expanded dashboard sections
 * @property notifications List of active notifications/alerts
 * @property lastRefreshTime Timestamp of last data refresh (epoch millis)
 */
data class DashboardUiState(
    // Core data
    val user: User,
    val streamState: StreamState = StreamState.Idle,
    val streamQuality: StreamQuality? = null,
    val cameraSettings: CameraSettings = CameraSettings.Default,
    val systemHealth: SystemHealth = SystemHealth.Default,
    val analyticsSummary: AnalyticsSummary = AnalyticsSummary.Empty,
    val deviceInfo: DeviceInfo = DeviceInfo.getCurrentDevice(),

    // UI state
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val expandedSections: Set<DashboardSection> = setOf(
        DashboardSection.STREAM_CONTROLS,
        DashboardSection.ANALYTICS
    ),
    val notifications: List<DashboardNotification> = emptyList(),
    val lastRefreshTime: Long = System.currentTimeMillis()
) {
    /**
     * Computed properties for UI convenience
     */

    /**
     * Check if there's an active error.
     */
    val hasError: Boolean
        get() = errorMessage != null

    /**
     * Check if there's a success message.
     */
    val hasSuccessMessage: Boolean
        get() = successMessage != null

    /**
     * Check if stream is currently active.
     */
    val isStreaming: Boolean
        get() = streamState.isActive

    /**
     * Check if stream can be started.
     */
    val canStartStream: Boolean
        get() = streamState.canStart && systemHealth.isHealthy && !isLoading

    /**
     * Check if stream can be stopped.
     */
    val canStopStream: Boolean
        get() = streamState.canStop && !isLoading

    /**
     * Check if camera settings can be modified.
     * Settings can only be changed when not streaming or during streaming
     * for certain safe properties.
     */
    val canModifyCameraSettings: Boolean
        get() = !isLoading

    /**
     * Check if system health is adequate for streaming.
     */
    val isSystemHealthy: Boolean
        get() = systemHealth.isHealthy

    /**
     * Get current streaming protocol if streaming.
     */
    val currentProtocol: StreamProtocol?
        get() = when (val state = streamState) {
            is StreamState.Streaming -> state.protocol
            is StreamState.Connecting -> state.protocol
            else -> null
        }

    /**
     * Get stream uptime in seconds if streaming.
     */
    val streamUptimeSeconds: Long?
        get() = when (val state = streamState) {
            is StreamState.Streaming -> {
                (System.currentTimeMillis() - state.startTime) / 1000
            }
            else -> null
        }

    /**
     * Format stream uptime as human-readable string.
     */
    val formattedStreamUptime: String?
        get() = streamUptimeSeconds?.let { seconds ->
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val secs = seconds % 60
            when {
                hours > 0 -> "%dh %02dm %02ds".format(hours, minutes, secs)
                minutes > 0 -> "%dm %02ds".format(minutes, secs)
                else -> "%ds".format(secs)
            }
        }

    /**
     * Get count of active alerts/notifications.
     */
    val activeNotificationCount: Int
        get() = notifications.size

    /**
     * Get count of critical issues.
     */
    val criticalIssueCount: Int
        get() = notifications.count { it.severity == NotificationSeverity.CRITICAL }

    /**
     * Check if a specific section is expanded.
     */
    fun isSectionExpanded(section: DashboardSection): Boolean =
        section in expandedSections

    /**
     * Get formatted last refresh time.
     */
    val formattedLastRefresh: String
        get() {
            val elapsed = System.currentTimeMillis() - lastRefreshTime
            val seconds = elapsed / 1000
            return when {
                seconds < 10 -> "Just now"
                seconds < 60 -> "$seconds seconds ago"
                else -> {
                    val minutes = seconds / 60
                    "$minutes minute${if (minutes > 1) "s" else ""} ago"
                }
            }
        }

    companion object {
        /**
         * Create initial dashboard state for a user.
         */
        fun initial(user: User) = DashboardUiState(
            user = user,
            streamState = StreamState.Idle,
            streamQuality = null,
            cameraSettings = CameraSettings.Default,
            systemHealth = SystemHealth.Default,
            analyticsSummary = AnalyticsSummary.Empty,
            deviceInfo = DeviceInfo.getCurrentDevice(),
            isLoading = false,
            errorMessage = null,
            successMessage = null,
            expandedSections = setOf(
                DashboardSection.STREAM_CONTROLS,
                DashboardSection.ANALYTICS
            ),
            notifications = emptyList(),
            lastRefreshTime = System.currentTimeMillis()
        )

        /**
         * Loading state during operations.
         */
        fun loading(currentState: DashboardUiState) = currentState.copy(
            isLoading = true,
            errorMessage = null,
            successMessage = null
        )

        /**
         * Error state when operation fails.
         */
        fun error(message: String, currentState: DashboardUiState) = currentState.copy(
            isLoading = false,
            errorMessage = message
        )

        /**
         * Success state after operation completes.
         */
        fun success(message: String, currentState: DashboardUiState) = currentState.copy(
            isLoading = false,
            errorMessage = null,
            successMessage = message
        )
    }
}

/**
 * Represents a notification or alert to display to the user.
 *
 * @property id Unique identifier for this notification
 * @property title Notification title
 * @property message Notification message
 * @property severity Severity level of the notification
 * @property timestamp When the notification was created (epoch millis)
 * @property isDismissible Whether user can dismiss this notification
 */
data class DashboardNotification(
    val id: String,
    val title: String,
    val message: String,
    val severity: NotificationSeverity = NotificationSeverity.INFO,
    val timestamp: Long = System.currentTimeMillis(),
    val isDismissible: Boolean = true
)

/**
 * Notification severity levels.
 */
enum class NotificationSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}
