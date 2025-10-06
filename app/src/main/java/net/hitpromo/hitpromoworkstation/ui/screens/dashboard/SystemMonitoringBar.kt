package net.hitpromo.hitpromoworkstation.ui.screens.dashboard

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.hitpromo.hitpromoworkstation.domain.model.SystemHealth
import net.hitpromo.hitpromoworkstation.ui.theme.AlertRed
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme
import net.hitpromo.hitpromoworkstation.ui.theme.InfoBlue
import net.hitpromo.hitpromoworkstation.ui.theme.SafetyGreen
import net.hitpromo.hitpromoworkstation.ui.theme.WarningAmber

/**
 * System Monitoring Bar showing key system health metrics.
 *
 * Displays four metric cards:
 * - Network: Signal strength and connection type
 * - CPU: CPU usage percentage
 * - Memory: Memory usage percentage
 * - Temperature: Device temperature
 *
 * Each card shows:
 * - Icon representing the metric
 * - Current value
 * - Status indicator with color-coded border
 * - Pulsing animation on warning/critical status
 *
 * Health status colors:
 * - Excellent: Green
 * - Good: Blue
 * - Warning: Orange (with pulse animation)
 * - Critical: Red (with pulse animation)
 *
 * @param systemHealth Current system health data
 * @param modifier Optional modifier for the bar
 */
@Composable
fun SystemMonitoringBar(
    systemHealth: SystemHealth,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.semantics {
            contentDescription = "System monitoring bar showing network, CPU, memory, and temperature"
        },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Network metric
            SystemMetricCard(
                icon = Icons.Default.CheckCircle,
                label = "Network",
                value = systemHealth.formattedNetworkStrength,
                subtitle = systemHealth.networkType.displayName,
                status = getNetworkStatus(systemHealth),
                modifier = Modifier.weight(1f)
            )

            // CPU metric
            SystemMetricCard(
                icon = Icons.Default.Star,
                label = "CPU",
                value = "${(systemHealth.cpuUsage * 100).toInt()}%",
                subtitle = "Usage",
                status = getCpuStatus(systemHealth),
                modifier = Modifier.weight(1f)
            )

            // Memory metric
            SystemMetricCard(
                icon = Icons.Default.Info,
                label = "Memory",
                value = "${(systemHealth.memoryUsage * 100).toInt()}%",
                subtitle = "${systemHealth.memoryAvailableMb} MB free",
                status = getMemoryStatus(systemHealth),
                modifier = Modifier.weight(1f)
            )

            // Temperature metric
            SystemMetricCard(
                icon = Icons.Default.Home,
                label = "Temp",
                value = systemHealth.formattedTemperature,
                subtitle = "Device",
                status = getTemperatureStatus(systemHealth),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual system metric card.
 */
@Composable
private fun SystemMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String,
    status: MetricStatus,
    modifier: Modifier = Modifier
) {
    val shouldPulse = status == MetricStatus.WARNING || status == MetricStatus.CRITICAL

    // Pulsing animation for warning/critical states
    val infiniteTransition = rememberInfiniteTransition(label = "metric_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (shouldPulse) 1.3f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "metric_scale"
    )

    Card(
        modifier = modifier.semantics {
            contentDescription = "$label: $value - ${status.displayName}"
        },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            color = status.color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator with icon
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Pulsing background circle for warning/critical
                if (shouldPulse) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .scale(scale)
                            .background(
                                color = status.color.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    )
                }

                // Icon
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = status.color
                )
            }

            // Metric info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = status.color
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Metric status with color and display name.
 */
private enum class MetricStatus(val color: Color, val displayName: String) {
    EXCELLENT(SafetyGreen, "Excellent"),
    GOOD(InfoBlue, "Good"),
    WARNING(WarningAmber, "Warning"),
    CRITICAL(AlertRed, "Critical")
}

/**
 * Determine network status based on signal strength and type.
 */
private fun getNetworkStatus(systemHealth: SystemHealth): MetricStatus {
    return when {
        systemHealth.networkStrength < 0.5f || !systemHealth.networkType.isReliable ->
            MetricStatus.CRITICAL
        systemHealth.networkStrength < 0.7f ->
            MetricStatus.WARNING
        systemHealth.networkStrength > 0.9f && systemHealth.networkType.isReliable ->
            MetricStatus.EXCELLENT
        else ->
            MetricStatus.GOOD
    }
}

/**
 * Determine CPU status based on usage.
 */
private fun getCpuStatus(systemHealth: SystemHealth): MetricStatus {
    return when {
        systemHealth.cpuUsage > 0.8f -> MetricStatus.CRITICAL
        systemHealth.cpuUsage > 0.6f -> MetricStatus.WARNING
        systemHealth.cpuUsage < 0.4f -> MetricStatus.EXCELLENT
        else -> MetricStatus.GOOD
    }
}

/**
 * Determine memory status based on usage.
 */
private fun getMemoryStatus(systemHealth: SystemHealth): MetricStatus {
    return when {
        systemHealth.memoryUsage > 0.85f -> MetricStatus.CRITICAL
        systemHealth.memoryUsage > 0.7f -> MetricStatus.WARNING
        systemHealth.memoryUsage < 0.5f -> MetricStatus.EXCELLENT
        else -> MetricStatus.GOOD
    }
}

/**
 * Determine temperature status.
 */
private fun getTemperatureStatus(systemHealth: SystemHealth): MetricStatus {
    return when {
        systemHealth.temperature > 45.0f -> MetricStatus.CRITICAL
        systemHealth.temperature > 40.0f -> MetricStatus.WARNING
        systemHealth.temperature < 38.0f -> MetricStatus.EXCELLENT
        else -> MetricStatus.GOOD
    }
}

// ========================================
// Preview Composables
// ========================================

@Preview(
    name = "System Monitoring Bar - Healthy",
    widthDp = 1920,
    heightDp = 120,
    showBackground = true
)
@Composable
fun SystemMonitoringBarHealthyPreview() {
    HitPromoWorkstationTheme {
        SystemMonitoringBar(
            systemHealth = SystemHealth.Default
        )
    }
}

@Preview(
    name = "System Monitoring Bar - Warning",
    widthDp = 1920,
    heightDp = 120,
    showBackground = true
)
@Composable
fun SystemMonitoringBarWarningPreview() {
    HitPromoWorkstationTheme {
        SystemMonitoringBar(
            systemHealth = SystemHealth(
                networkStrength = 0.6f,
                networkType = SystemHealth.NetworkType.WIFI,
                cpuUsage = 0.75f,
                memoryUsage = 0.8f,
                memoryAvailableMb = 512,
                temperature = 42.0f,
                batteryLevel = 0.4f,
                isCharging = false,
                storageAvailableGb = 5.2f,
                uptimeSeconds = 28800
            )
        )
    }
}

@Preview(
    name = "System Monitoring Bar - Critical",
    widthDp = 1920,
    heightDp = 120,
    showBackground = true
)
@Composable
fun SystemMonitoringBarCriticalPreview() {
    HitPromoWorkstationTheme {
        SystemMonitoringBar(
            systemHealth = SystemHealth.Critical
        )
    }
}
