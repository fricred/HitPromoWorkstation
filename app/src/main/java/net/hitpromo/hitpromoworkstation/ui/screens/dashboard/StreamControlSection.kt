package net.hitpromo.hitpromoworkstation.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.hitpromo.hitpromoworkstation.domain.model.StreamProtocol
import net.hitpromo.hitpromoworkstation.domain.model.StreamState
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialButton
import net.hitpromo.hitpromoworkstation.ui.theme.AlertRed
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme
import net.hitpromo.hitpromoworkstation.ui.theme.SafetyGreen

/**
 * Stream Control Section with large control buttons.
 *
 * Provides three primary stream control actions:
 * - Start Stream (with protocol selection)
 * - Stop Stream
 * - Emergency Stop
 *
 * Each button is 88dp height minimum for industrial gloved operation.
 * Buttons are enabled/disabled based on current stream state.
 *
 * @param streamState Current stream state
 * @param canStartStream Whether stream can be started (based on system health)
 * @param canStopStream Whether stream can be stopped
 * @param onStartStream Callback when start stream is clicked (with selected protocol)
 * @param onStopStream Callback when stop stream is clicked
 * @param onEmergencyStop Callback when emergency stop is clicked
 * @param modifier Optional modifier for the section
 */
@Composable
fun StreamControlSection(
    streamState: StreamState,
    canStartStream: Boolean,
    canStopStream: Boolean,
    onStartStream: (StreamProtocol) -> Unit,
    onStopStream: () -> Unit,
    onEmergencyStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .semantics {
                contentDescription = "Stream control section with start, stop, and emergency buttons"
            }
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        Text(
            text = "Stream Controls",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        // Start Stream Button (only visible when not streaming)
        if (streamState is StreamState.Idle || streamState is StreamState.Error) {
            StartStreamButton(
                enabled = canStartStream,
                onStartStream = onStartStream,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Stop Stream Button (visible when streaming or connecting)
        if (streamState is StreamState.Streaming || streamState is StreamState.Connecting) {
            StopStreamButton(
                enabled = canStopStream,
                onStopStream = onStopStream,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Emergency Stop Button (always visible)
        EmergencyStopButton(
            enabled = canStopStream,
            onEmergencyStop = onEmergencyStop,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Start Stream button with protocol selection.
 */
@Composable
private fun StartStreamButton(
    enabled: Boolean,
    onStartStream: (StreamProtocol) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProtocol by remember { mutableStateOf(StreamProtocol.WEBRTC) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Protocol selection row
        Text(
            text = "Select Protocol:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // WebRTC protocol button
            IndustrialButton(
                onClick = { selectedProtocol = StreamProtocol.WEBRTC },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .semantics {
                        contentDescription = "Select WebRTC protocol"
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedProtocol == StreamProtocol.WEBRTC)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedProtocol == StreamProtocol.WEBRTC)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = "WebRTC",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // RTSP protocol button
            IndustrialButton(
                onClick = { selectedProtocol = StreamProtocol.RTSP },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .semantics {
                        contentDescription = "Select RTSP protocol"
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedProtocol == StreamProtocol.RTSP)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedProtocol == StreamProtocol.RTSP)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = "RTSP",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Start button
        IndustrialButton(
            onClick = { onStartStream(selectedProtocol) },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp) // Large touch target
                .semantics {
                    contentDescription = "Start streaming with ${selectedProtocol.displayName}"
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = SafetyGreen,
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Start Stream",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

/**
 * Stop Stream button.
 */
@Composable
private fun StopStreamButton(
    enabled: Boolean,
    onStopStream: () -> Unit,
    modifier: Modifier = Modifier
) {
    IndustrialButton(
        onClick = onStopStream,
        enabled = enabled,
        modifier = modifier
            .height(88.dp) // Large touch target
            .semantics {
                contentDescription = "Stop streaming"
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF44336), // Red for stop
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Stop Stream",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

/**
 * Emergency Stop button.
 */
@Composable
private fun EmergencyStopButton(
    enabled: Boolean,
    onEmergencyStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    IndustrialButton(
        onClick = onEmergencyStop,
        enabled = enabled,
        modifier = modifier
            .height(88.dp) // Large touch target
            .semantics {
                contentDescription = "Emergency stop - Immediately terminate stream"
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF5722), // Deep orange for emergency
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "EMERGENCY STOP",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Text(
                text = "Immediate termination",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

// ========================================
// Preview Composables
// ========================================

@Preview(
    name = "Stream Controls - Idle",
    widthDp = 864,
    heightDp = 400,
    showBackground = true
)
@Composable
fun StreamControlSectionIdlePreview() {
    HitPromoWorkstationTheme {
        StreamControlSection(
            streamState = StreamState.Idle,
            canStartStream = true,
            canStopStream = false,
            onStartStream = {},
            onStopStream = {},
            onEmergencyStop = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "Stream Controls - Streaming",
    widthDp = 864,
    heightDp = 400,
    showBackground = true
)
@Composable
fun StreamControlSectionStreamingPreview() {
    HitPromoWorkstationTheme {
        StreamControlSection(
            streamState = StreamState.Streaming(
                protocol = StreamProtocol.WEBRTC,
                quality = net.hitpromo.hitpromoworkstation.domain.model.StreamQuality.Default,
                startTime = System.currentTimeMillis(),
                bytesTransferred = 0L
            ),
            canStartStream = false,
            canStopStream = true,
            onStartStream = {},
            onStopStream = {},
            onEmergencyStop = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "Stream Controls - Error",
    widthDp = 864,
    heightDp = 400,
    showBackground = true
)
@Composable
fun StreamControlSectionErrorPreview() {
    HitPromoWorkstationTheme {
        StreamControlSection(
            streamState = StreamState.Error(
                message = "Connection failed",
                isRecoverable = true,
                previousState = StreamState.Idle
            ),
            canStartStream = true,
            canStopStream = false,
            onStartStream = {},
            onStopStream = {},
            onEmergencyStop = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
