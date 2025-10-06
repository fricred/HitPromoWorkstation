package net.hitpromo.hitpromoworkstation.domain.repository

import kotlinx.coroutines.flow.Flow
import net.hitpromo.hitpromoworkstation.domain.model.CameraSettings
import net.hitpromo.hitpromoworkstation.domain.model.StreamProtocol
import net.hitpromo.hitpromoworkstation.domain.model.StreamQuality
import net.hitpromo.hitpromoworkstation.domain.model.StreamState

/**
 * Repository interface for video streaming operations.
 *
 * Defines the contract for managing video stream lifecycle, configuration,
 * and real-time quality monitoring. Implementation will handle camera access,
 * encoding, and transmission to AWS Kinesis Video Streams.
 */
interface StreamRepository {

    /**
     * Observable stream of current streaming state.
     * Emits state changes throughout the streaming lifecycle.
     */
    val streamState: Flow<StreamState>

    /**
     * Observable stream of real-time quality metrics.
     * Only emits when actively streaming.
     */
    val streamQuality: Flow<StreamQuality>

    /**
     * Current camera settings.
     */
    val cameraSettings: Flow<CameraSettings>

    /**
     * Start video streaming with specified protocol.
     *
     * Initializes camera, starts encoding, and begins transmission to the
     * configured streaming endpoint.
     *
     * @param protocol Streaming protocol to use (WebRTC or RTSP)
     * @return Result indicating success or failure with error details
     */
    suspend fun startStream(protocol: StreamProtocol): Result<Unit>

    /**
     * Stop active video streaming.
     *
     * Gracefully stops the stream, releases camera resources, and cleans up
     * encoder and network connections.
     *
     * @return Result indicating success or failure with error details
     */
    suspend fun stopStream(): Result<Unit>

    /**
     * Update camera settings.
     *
     * Applies new camera configuration. Some settings may require stopping
     * and restarting the stream to take effect.
     *
     * @param settings New camera settings to apply
     * @return Result indicating success or failure with error details
     */
    suspend fun updateCameraSettings(settings: CameraSettings): Result<Unit>

    /**
     * Switch between available cameras (if device has multiple cameras).
     *
     * @param cameraId Camera identifier to switch to
     * @return Result indicating success or failure with error details
     */
    suspend fun switchCamera(cameraId: String): Result<Unit>

    /**
     * Get list of available cameras on the device.
     *
     * @return List of camera IDs with their descriptions
     */
    suspend fun getAvailableCameras(): List<Pair<String, String>>

    /**
     * Check if streaming is currently active.
     *
     * @return true if stream is in Streaming state, false otherwise
     */
    suspend fun isStreaming(): Boolean

    /**
     * Get current streaming protocol.
     *
     * @return Active protocol or null if not streaming
     */
    suspend fun getCurrentProtocol(): StreamProtocol?

    /**
     * Restart the stream with current settings.
     * Convenience method that stops and starts the stream.
     *
     * @return Result indicating success or failure with error details
     */
    suspend fun restartStream(): Result<Unit>

    /**
     * Get total bytes transferred in current/last session.
     *
     * @return Total bytes transferred
     */
    suspend fun getBytesTransferred(): Long

    /**
     * Get stream uptime in seconds.
     *
     * @return Seconds since stream started, or 0 if not streaming
     */
    suspend fun getStreamUptime(): Long
}
