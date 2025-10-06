package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Supported video streaming protocols.
 *
 * Defines the available protocols for video transmission from the tablet
 * to AWS Kinesis Video Streams or other streaming endpoints.
 */
enum class StreamProtocol {
    /**
     * WebRTC protocol via AWS Kinesis Video Streams WebRTC SDK.
     * Provides low-latency bidirectional communication.
     * Recommended for real-time analytics and interactive use cases.
     */
    WEBRTC,

    /**
     * Real-Time Streaming Protocol (RTSP).
     * Traditional streaming protocol with broader compatibility.
     * Provides reliable streaming over TCP with lower overhead.
     */
    RTSP;

    /**
     * Human-readable display name for the protocol.
     */
    val displayName: String
        get() = when (this) {
            WEBRTC -> "WebRTC"
            RTSP -> "RTSP"
        }

    /**
     * Technical description of the protocol.
     */
    val description: String
        get() = when (this) {
            WEBRTC -> "Low-latency WebRTC via AWS Kinesis"
            RTSP -> "RTSP streaming over TCP"
        }

    /**
     * Typical latency range for this protocol in milliseconds.
     */
    val typicalLatencyMs: IntRange
        get() = when (this) {
            WEBRTC -> 100..500
            RTSP -> 500..2000
        }

    /**
     * Whether this protocol supports bidirectional communication.
     */
    val isBidirectional: Boolean
        get() = when (this) {
            WEBRTC -> true
            RTSP -> false
        }
}
