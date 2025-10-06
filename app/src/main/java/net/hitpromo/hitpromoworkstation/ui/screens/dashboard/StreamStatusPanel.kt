package net.hitpromo.hitpromoworkstation.ui.screens.dashboard

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.hitpromo.hitpromoworkstation.domain.model.AnalyticsSummary
import net.hitpromo.hitpromoworkstation.domain.model.StreamProtocol
import net.hitpromo.hitpromoworkstation.domain.model.StreamQuality
import net.hitpromo.hitpromoworkstation.domain.model.StreamState
import net.hitpromo.hitpromoworkstation.ui.theme.AlertRed
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme
import net.hitpromo.hitpromoworkstation.ui.theme.InfoBlue
import net.hitpromo.hitpromoworkstation.ui.theme.SafetyGreen
import net.hitpromo.hitpromoworkstation.ui.theme.WarningAmber

/**
 * Stream Status Panel showing current stream state, connection details, and analytics.
 *
 * Displays:
 * - Stream status badge with animated indicator
 * - Connection details (protocol, quality, bitrate, latency)
 * - Analytics summary (detections count, object breakdown)
 * - Color-coded status (Green=streaming, Orange=connecting, Gray=stopped, Red=error)
 *
 * @param streamState Current stream state
 * @param streamQuality Stream quality metrics (null when not streaming)
 * @param currentProtocol Current streaming protocol (null when not streaming)
 * @param analyticsSummary Real-time analytics data
 * @param streamUptime Formatted stream uptime string (null when not streaming)
 * @param modifier Optional modifier for the panel
 */
@Composable
fun StreamStatusPanel(
    streamState: StreamState,
    streamQuality: StreamQuality?,
    currentProtocol: StreamProtocol?,
    analyticsSummary: AnalyticsSummary,
    streamUptime: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.semantics {
            contentDescription = "Stream status panel showing connection details and analytics"
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stream Status Badge
            StreamStatusBadge(
                streamState = streamState,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Connection Details
            if (streamState is StreamState.Streaming || streamState is StreamState.Connecting) {
                ConnectionDetailsSection(
                    streamState = streamState,
                    streamQuality = streamQuality,
                    currentProtocol = currentProtocol,
                    streamUptime = streamUptime
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            // Analytics Summary
            AnalyticsSummarySection(
                analyticsSummary = analyticsSummary,
                isStreaming = streamState is StreamState.Streaming
            )
        }
    }
}

/**
 * Stream status badge with animated indicator.
 */
@Composable
private fun StreamStatusBadge(
    streamState: StreamState,
    modifier: Modifier = Modifier
) {
    val (statusText, statusColor, statusIcon) = when (streamState) {
        is StreamState.Idle -> Triple("Stream Idle", Color.Gray, null)
        is StreamState.Connecting -> Triple("Connecting...", WarningAmber, "connecting")
        is StreamState.Streaming -> Triple("Streaming Active", SafetyGreen, "streaming")
        is StreamState.Stopping -> Triple("Stopping...", WarningAmber, null)
        is StreamState.Error -> Triple("Stream Error", AlertRed, "error")
    }

    Surface(
        modifier = modifier.semantics {
            contentDescription = "Stream status: $statusText"
        },
        shape = RoundedCornerShape(12.dp),
        color = statusColor.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated status indicator
            when (statusIcon) {
                "streaming" -> {
                    // Pulsing green dot for streaming
                    val infiniteTransition = rememberInfiniteTransition(label = "streaming_pulse")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = 0.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 800),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "streaming_alpha"
                    )
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(alpha * 360),
                        tint = SafetyGreen
                    )
                }
                "connecting" -> {
                    // Rotating sync icon for connecting
                    val infiniteTransition = rememberInfiniteTransition(label = "connecting_rotate")
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1200),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "connecting_rotation"
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(rotation),
                        tint = WarningAmber
                    )
                }
                "error" -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = AlertRed
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(statusColor, CircleShape)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Stream Status",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = statusColor
                )
            }
        }
    }
}

/**
 * Connection details section.
 */
@Composable
private fun ConnectionDetailsSection(
    streamState: StreamState,
    streamQuality: StreamQuality?,
    currentProtocol: StreamProtocol?,
    streamUptime: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Connection Details",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        // Protocol
        currentProtocol?.let { protocol ->
            DetailRow(
                label = "Protocol",
                value = protocol.displayName
            )
        }

        // Quality metrics
        streamQuality?.let { quality ->
            DetailRow(
                label = "Quality",
                value = quality.qualityDescription,
                valueColor = when {
                    quality.qualityPercentage >= 80 -> SafetyGreen
                    quality.qualityPercentage >= 60 -> InfoBlue
                    quality.qualityPercentage >= 40 -> WarningAmber
                    else -> AlertRed
                }
            )
            DetailRow(
                label = "Resolution",
                value = quality.resolution
            )
            DetailRow(
                label = "FPS",
                value = "${quality.fps}"
            )
            DetailRow(
                label = "Bitrate",
                value = quality.formattedBitrate
            )
            DetailRow(
                label = "Latency",
                value = "${quality.latencyMs} ms",
                valueColor = when {
                    quality.latencyMs < 300 -> SafetyGreen
                    quality.latencyMs < 600 -> WarningAmber
                    else -> AlertRed
                }
            )
        }

        // Uptime
        streamUptime?.let { uptime ->
            DetailRow(
                label = "Uptime",
                value = uptime
            )
        }

        // Bytes transferred (for Streaming state)
        if (streamState is StreamState.Streaming) {
            val bytesTransferredMB = streamState.bytesTransferred / (1024 * 1024)
            DetailRow(
                label = "Data Transferred",
                value = "$bytesTransferredMB MB"
            )
        }
    }
}

/**
 * Analytics summary section.
 */
@Composable
private fun AnalyticsSummarySection(
    analyticsSummary: AnalyticsSummary,
    isStreaming: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Analytics Summary",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        if (analyticsSummary.hasData) {
            DetailRow(
                label = "Total Detections",
                value = "${analyticsSummary.totalDetections}",
                valueColor = SafetyGreen
            )
            DetailRow(
                label = "Detection Rate",
                value = analyticsSummary.formattedDetectionRate
            )
            DetailRow(
                label = "Avg Confidence",
                value = analyticsSummary.formattedConfidence
            )

            if (analyticsSummary.alerts > 0) {
                DetailRow(
                    label = "Alerts",
                    value = "${analyticsSummary.alerts}",
                    valueColor = AlertRed
                )
            }

            // Top detected objects
            if (analyticsSummary.topDetectedObjects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Object Breakdown",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                analyticsSummary.topDetectedObjects.take(3).forEach { (objectType, count) ->
                    ObjectCountRow(
                        objectType = objectType,
                        count = count,
                        percentage = analyticsSummary.getPercentageForObject(objectType)
                    )
                }
            }
        } else {
            Text(
                text = if (isStreaming)
                    "Waiting for analytics data..."
                else
                    "No analytics data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Detail row with label and value.
 */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = valueColor
        )
    }
}

/**
 * Object count row with type, count, and percentage.
 */
@Composable
private fun ObjectCountRow(
    objectType: String,
    count: Int,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = objectType.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "(${String.format("%.1f", percentage)}%)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ========================================
// Preview Composables
// ========================================

@Preview(
    name = "Stream Status Panel - Idle",
    widthDp = 864,
    heightDp = 600,
    showBackground = true
)
@Composable
fun StreamStatusPanelIdlePreview() {
    HitPromoWorkstationTheme {
        StreamStatusPanel(
            streamState = StreamState.Idle,
            streamQuality = null,
            currentProtocol = null,
            analyticsSummary = AnalyticsSummary.Empty,
            streamUptime = null
        )
    }
}

@Preview(
    name = "Stream Status Panel - Streaming",
    widthDp = 864,
    heightDp = 600,
    showBackground = true
)
@Composable
fun StreamStatusPanelStreamingPreview() {
    HitPromoWorkstationTheme {
        StreamStatusPanel(
            streamState = StreamState.Streaming(
                protocol = StreamProtocol.WEBRTC,
                quality = StreamQuality.Default,
                startTime = System.currentTimeMillis() - 300000, // 5 minutes ago
                bytesTransferred = 157_286_400L
            ),
            streamQuality = StreamQuality.Default,
            currentProtocol = StreamProtocol.WEBRTC,
            analyticsSummary = AnalyticsSummary.Sample,
            streamUptime = "5m 00s"
        )
    }
}
