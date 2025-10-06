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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.hitpromo.hitpromoworkstation.domain.model.CameraSettings
import net.hitpromo.hitpromoworkstation.domain.model.StreamProtocol
import net.hitpromo.hitpromoworkstation.domain.model.StreamState
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialButton
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialSecondaryButton
import net.hitpromo.hitpromoworkstation.ui.theme.AlertRed
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme

/**
 * Camera Preview Section with overlay info and controls.
 *
 * Displays camera preview (placeholder for now) with:
 * - Recording indicator (pulsing red dot when streaming)
 * - Camera overlay info (resolution, focus mode, flash status)
 * - Camera control buttons (Auto Focus, Flash, Settings)
 *
 * Optimized for industrial use with large touch targets and clear visual feedback.
 *
 * @param cameraSettings Current camera configuration
 * @param streamState Current stream state
 * @param onToggleFlash Callback when flash toggle is clicked
 * @param onToggleAutoFocus Callback when auto-focus toggle is clicked
 * @param onOpenCameraSettings Callback when settings button is clicked
 * @param modifier Optional modifier for the section
 */
@Composable
fun CameraPreviewSection(
    cameraSettings: CameraSettings,
    streamState: StreamState,
    onToggleFlash: () -> Unit,
    onToggleAutoFocus: () -> Unit,
    onOpenCameraSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.semantics {
            contentDescription = "Camera preview section"
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Camera preview placeholder (black background for now)
            CameraPreviewPlaceholder(
                modifier = Modifier.fillMaxSize()
            )

            // Top overlay: Recording indicator and camera info
            CameraOverlayInfo(
                cameraSettings = cameraSettings,
                isRecording = streamState is StreamState.Streaming,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Bottom overlay: Camera control buttons
            CameraControlButtons(
                cameraSettings = cameraSettings,
                onToggleFlash = onToggleFlash,
                onToggleAutoFocus = onToggleAutoFocus,
                onOpenCameraSettings = onOpenCameraSettings,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

/**
 * Camera preview placeholder.
 * TODO: Replace with actual CameraX preview when camera integration is implemented.
 */
@Composable
private fun CameraPreviewPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.Black)
            .semantics {
                contentDescription = "Camera preview placeholder - Camera integration pending"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = Color.White.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Camera Preview",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = "Camera integration pending",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

/**
 * Camera overlay info at the top showing recording status and camera settings.
 */
@Composable
private fun CameraOverlayInfo(
    cameraSettings: CameraSettings,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Left side: Recording indicator
        if (isRecording) {
            RecordingIndicator()
        } else {
            Spacer(modifier = Modifier.width(1.dp)) // Maintain layout
        }

        // Right side: Camera info badges
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CameraInfoBadge(label = "Resolution", value = cameraSettings.resolution)
            CameraInfoBadge(
                label = "Focus",
                value = if (cameraSettings.autoFocus) "Auto" else "Manual"
            )
            if (cameraSettings.flashEnabled) {
                CameraInfoBadge(label = "Flash", value = "ON")
            }
        }
    }
}

/**
 * Pulsing recording indicator (red dot).
 */
@Composable
private fun RecordingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "recording_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recording_alpha"
    )

    Surface(
        modifier = Modifier
            .semantics {
                contentDescription = "Recording indicator - Stream is active"
            },
        shape = RoundedCornerShape(24.dp),
        color = Color.Black.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .alpha(alpha),
                tint = AlertRed
            )
            Text(
                text = "REC",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
    }
}

/**
 * Info badge showing camera settings.
 */
@Composable
private fun CameraInfoBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.semantics {
            contentDescription = "$label: $value"
        },
        shape = RoundedCornerShape(8.dp),
        color = Color.Black.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
    }
}

/**
 * Camera control buttons at the bottom.
 */
@Composable
private fun CameraControlButtons(
    cameraSettings: CameraSettings,
    onToggleFlash: () -> Unit,
    onToggleAutoFocus: () -> Unit,
    onOpenCameraSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Auto Focus button
        IndustrialSecondaryButton(
            onClick = onToggleAutoFocus,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription =
                        "Toggle auto-focus - Currently ${if (cameraSettings.autoFocus) "enabled" else "disabled"}"
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (cameraSettings.autoFocus) "Auto Focus: ON" else "Auto Focus: OFF",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Flash button
        IndustrialSecondaryButton(
            onClick = onToggleFlash,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription =
                        "Toggle flash - Currently ${if (cameraSettings.flashEnabled) "enabled" else "disabled"}"
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (cameraSettings.flashEnabled)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (cameraSettings.flashEnabled) "Flash: ON" else "Flash: OFF",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Settings button
        IndustrialSecondaryButton(
            onClick = onOpenCameraSettings,
            modifier = Modifier
                .weight(1f)
                .semantics {
                    contentDescription = "Open camera settings"
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

// ========================================
// Preview Composables
// ========================================

@Preview(
    name = "Camera Preview - Idle",
    widthDp = 1056,
    heightDp = 1080,
    showBackground = true
)
@Composable
fun CameraPreviewSectionIdlePreview() {
    HitPromoWorkstationTheme {
        CameraPreviewSection(
            cameraSettings = CameraSettings.Default,
            streamState = StreamState.Idle,
            onToggleFlash = {},
            onToggleAutoFocus = {},
            onOpenCameraSettings = {}
        )
    }
}

@Preview(
    name = "Camera Preview - Recording",
    widthDp = 1056,
    heightDp = 1080,
    showBackground = true
)
@Composable
fun CameraPreviewSectionRecordingPreview() {
    HitPromoWorkstationTheme {
        CameraPreviewSection(
            cameraSettings = CameraSettings.Default.copy(
                flashEnabled = true,
                autoFocus = true
            ),
            streamState = StreamState.Streaming(
                protocol = StreamProtocol.WEBRTC,
                quality = net.hitpromo.hitpromoworkstation.domain.model.StreamQuality.Default,
                startTime = System.currentTimeMillis(),
                bytesTransferred = 0L
            ),
            onToggleFlash = {},
            onToggleAutoFocus = {},
            onOpenCameraSettings = {}
        )
    }
}
