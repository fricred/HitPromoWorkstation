package net.hitpromo.hitpromoworkstation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.hitpromo.hitpromoworkstation.domain.usecase.PasswordStrength
import net.hitpromo.hitpromoworkstation.ui.theme.AlertRed
import net.hitpromo.hitpromoworkstation.ui.theme.SafetyGreen
import net.hitpromo.hitpromoworkstation.ui.theme.WarningAmber

/**
 * Industrial-themed password strength indicator component.
 *
 * Displays a visual progress bar with color coding (red/yellow/green) to indicate
 * password strength. Designed for production floor visibility with large UI elements
 * and high contrast colors.
 *
 * @param passwordStrength The calculated password strength
 * @param modifier Modifier for the component
 * @param showLabel Whether to show the strength label text
 */
@Composable
fun PasswordStrengthIndicator(
    passwordStrength: PasswordStrength,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val (progress, color, label) = when (passwordStrength) {
        PasswordStrength.WEAK -> Triple(0.25f, AlertRed, "Weak")
        PasswordStrength.MEDIUM -> Triple(0.5f, WarningAmber, "Medium")
        PasswordStrength.STRONG -> Triple(0.75f, SafetyGreen, "Strong")
        PasswordStrength.VERY_STRONG -> Triple(1.0f, SafetyGreen, "Very Strong")
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(durationMillis = 300),
        label = "color"
    )

    Column(
        modifier = modifier
            .semantics {
                contentDescription = "Password strength: $label"
            }
    ) {
        if (showLabel) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Password Strength",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = animatedColor
                )
            }
        }

        // Custom strength bar with industrial styling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(animatedColor)
            )
        }

        // Optional: Show strength indicators as segments
        if (showLabel) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StrengthSegmentLabel("Weak", passwordStrength == PasswordStrength.WEAK)
                StrengthSegmentLabel("Medium", passwordStrength == PasswordStrength.MEDIUM)
                StrengthSegmentLabel("Strong", passwordStrength == PasswordStrength.STRONG || passwordStrength == PasswordStrength.VERY_STRONG)
            }
        }
    }
}

/**
 * Label for strength segments.
 */
@Composable
private fun StrengthSegmentLabel(
    text: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = if (isActive) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        modifier = modifier
    )
}

/**
 * Compact version for smaller spaces (no labels).
 */
@Composable
fun CompactPasswordStrengthIndicator(
    passwordStrength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    val (progress, color, label) = when (passwordStrength) {
        PasswordStrength.WEAK -> Triple(0.25f, AlertRed, "Weak")
        PasswordStrength.MEDIUM -> Triple(0.5f, WarningAmber, "Medium")
        PasswordStrength.STRONG -> Triple(0.75f, SafetyGreen, "Strong")
        PasswordStrength.VERY_STRONG -> Triple(1.0f, SafetyGreen, "Very Strong")
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(durationMillis = 300),
        label = "color"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .semantics {
                contentDescription = "Password strength: $label"
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(animatedColor)
        )
    }
}

/**
 * Segmented strength indicator with distinct colored bars.
 */
@Composable
fun SegmentedPasswordStrengthIndicator(
    passwordStrength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    val activeSegments = when (passwordStrength) {
        PasswordStrength.WEAK -> 1
        PasswordStrength.MEDIUM -> 2
        PasswordStrength.STRONG -> 3
        PasswordStrength.VERY_STRONG -> 4
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Password strength: ${passwordStrength.name}"
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(4) { index ->
            val isActive = index < activeSegments
            val color = when {
                !isActive -> MaterialTheme.colorScheme.surfaceVariant
                index == 0 -> AlertRed
                index == 1 -> WarningAmber
                else -> SafetyGreen
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}