package net.hitpromo.hitpromoworkstation.domain.repository

import kotlinx.coroutines.flow.Flow
import net.hitpromo.hitpromoworkstation.domain.model.AnalyticsSummary
import net.hitpromo.hitpromoworkstation.domain.model.DeviceInfo
import net.hitpromo.hitpromoworkstation.domain.model.SystemHealth

/**
 * Repository interface for system monitoring and analytics.
 *
 * Defines the contract for monitoring device health, retrieving analytics data,
 * and managing device information. Provides real-time streams of system metrics
 * for dashboard display and alerting.
 */
interface SystemMonitorRepository {

    /**
     * Observable stream of system health metrics.
     * Emits updated metrics at regular intervals (e.g., every 5 seconds).
     */
    val systemHealth: Flow<SystemHealth>

    /**
     * Observable stream of analytics summary data.
     * Emits updated analytics as new detections are processed.
     */
    val analyticsSummary: Flow<AnalyticsSummary>

    /**
     * Device information for this tablet.
     */
    val deviceInfo: Flow<DeviceInfo>

    /**
     * Get current system health snapshot.
     *
     * @return Current system health metrics
     */
    suspend fun getCurrentHealth(): SystemHealth

    /**
     * Get current analytics summary snapshot.
     *
     * @return Current analytics data
     */
    suspend fun getCurrentAnalytics(): AnalyticsSummary

    /**
     * Get device information.
     *
     * @return Device metadata and status
     */
    suspend fun getDeviceInfo(): DeviceInfo

    /**
     * Update device status.
     *
     * @param status New device status to set
     * @return Result indicating success or failure
     */
    suspend fun updateDeviceStatus(status: DeviceInfo.DeviceStatus): Result<Unit>

    /**
     * Update device location.
     *
     * @param location Physical location identifier
     * @return Result indicating success or failure
     */
    suspend fun updateDeviceLocation(location: String): Result<Unit>

    /**
     * Sync device information with backend.
     *
     * @return Result indicating success or failure
     */
    suspend fun syncDeviceInfo(): Result<Unit>

    /**
     * Reset analytics counters.
     * Clears all detection counts and starts a new analytics session.
     *
     * @return Result indicating success or failure
     */
    suspend fun resetAnalytics(): Result<Unit>

    /**
     * Get historical analytics for a time range.
     *
     * @param startTime Start of time range (epoch millis)
     * @param endTime End of time range (epoch millis)
     * @return List of analytics snapshots for the time range
     */
    suspend fun getHistoricalAnalytics(
        startTime: Long,
        endTime: Long
    ): Result<List<AnalyticsSummary>>

    /**
     * Check if system meets minimum requirements for streaming.
     *
     * @return true if system health is adequate, false otherwise
     */
    suspend fun canStream(): Boolean

    /**
     * Get list of current system warnings/issues.
     *
     * @return List of warning messages
     */
    suspend fun getSystemWarnings(): List<String>

    /**
     * Start continuous system monitoring.
     * Begins collecting and emitting system metrics.
     */
    suspend fun startMonitoring()

    /**
     * Stop continuous system monitoring.
     * Stops collecting system metrics to conserve resources.
     */
    suspend fun stopMonitoring()
}
