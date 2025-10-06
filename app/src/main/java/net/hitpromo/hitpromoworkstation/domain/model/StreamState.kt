package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Represents the current state of the video streaming service.
 *
 * This sealed class models all possible states of the streaming lifecycle,
 * from initial idle state through active streaming to error conditions.
 */
sealed class StreamState {
    /**
     * Stream is idle and not connected.
     * Initial state when the application starts or after stream is stopped.
     */
    data object Idle : StreamState()

    /**
     * Stream is establishing connection.
     * Intermediate state during startup, negotiating protocols and initializing camera.
     *
     * @property protocol The streaming protocol being used
     * @property progress Optional progress indicator (0.0 to 1.0)
     */
    data class Connecting(
        val protocol: StreamProtocol,
        val progress: Float = 0.0f
    ) : StreamState()

    /**
     * Stream is actively streaming video.
     *
     * @property protocol The active streaming protocol
     * @property quality Current stream quality metrics
     * @property startTime Timestamp when streaming started (epoch millis)
     * @property bytesTransferred Total bytes transferred since stream started
     */
    data class Streaming(
        val protocol: StreamProtocol,
        val quality: StreamQuality,
        val startTime: Long,
        val bytesTransferred: Long = 0L
    ) : StreamState()

    /**
     * Stream is being stopped gracefully.
     * Intermediate state during shutdown, cleaning up resources.
     */
    data object Stopping : StreamState()

    /**
     * Stream encountered an error.
     *
     * @property message Human-readable error message
     * @property cause Optional exception that caused the error
     * @property isRecoverable Whether the error can be recovered by retrying
     * @property previousState The state before the error occurred
     */
    data class Error(
        val message: String,
        val cause: Throwable? = null,
        val isRecoverable: Boolean = true,
        val previousState: StreamState = Idle
    ) : StreamState()
}

/**
 * Extension properties for StreamState for convenience checks.
 */
val StreamState.isActive: Boolean
    get() = this is StreamState.Streaming

val StreamState.isTransitioning: Boolean
    get() = this is StreamState.Connecting || this is StreamState.Stopping

val StreamState.canStart: Boolean
    get() = this is StreamState.Idle || (this is StreamState.Error && isRecoverable)

val StreamState.canStop: Boolean
    get() = this is StreamState.Streaming || this is StreamState.Connecting
