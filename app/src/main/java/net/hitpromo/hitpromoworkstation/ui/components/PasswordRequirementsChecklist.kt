package net.hitpromo.hitpromoworkstation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.hitpromo.hitpromoworkstation.domain.usecase.PasswordRequirements
import net.hitpromo.hitpromoworkstation.ui.theme.SafetyGreen
import net.hitpromo.hitpromoworkstation.ui.theme.AlertRed

/**
 * Industrial-themed password requirements checklist component.
 *
 * Displays password requirements with visual indicators (checkmarks/circles)
 * showing which requirements are met. Designed for production floor visibility
 * with large text and high contrast colors.
 *
 * @param password The current password being validated
 * @param confirmPassword The confirm password field (for matching check)
 * @param modifier Modifier for the component
 */
@Composable
fun PasswordRequirementsChecklist(
    password: String,
    confirmPassword: String,
    modifier: Modifier = Modifier
) {
    // Calculate requirements
    val requirements = PasswordRequirements(
        hasMinLength = password.length >= 8,
        hasUppercase = password.any { it.isUpperCase() },
        hasLowercase = password.any { it.isLowerCase() },
        hasDigit = password.any { it.isDigit() },
        hasSpecialChar = password.any { !it.isLetterOrDigit() }
    )

    val passwordsMatch = password.isNotEmpty() &&
            confirmPassword.isNotEmpty() &&
            password == confirmPassword

    Column(
        modifier = modifier
            .semantics {
                contentDescription = "Password requirements checklist"
            },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Password Requirements",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        RequirementItem(
            text = "At least 8 characters",
            isMet = requirements.hasMinLength,
            isRequired = true
        )

        RequirementItem(
            text = "One uppercase letter (A-Z)",
            isMet = requirements.hasUppercase,
            isRequired = true
        )

        RequirementItem(
            text = "One lowercase letter (a-z)",
            isMet = requirements.hasLowercase,
            isRequired = true
        )

        RequirementItem(
            text = "One number (0-9)",
            isMet = requirements.hasDigit,
            isRequired = true
        )

        RequirementItem(
            text = "One special character (!@#$%^&*)",
            isMet = requirements.hasSpecialChar,
            isRequired = true
        )

        // Password match requirement (only show if confirm password has content)
        AnimatedVisibility(
            visible = confirmPassword.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            RequirementItem(
                text = "Passwords match",
                isMet = passwordsMatch,
                isRequired = true
            )
        }
    }
}

/**
 * Individual requirement item with checkmark or circle indicator.
 *
 * @param text The requirement description
 * @param isMet Whether the requirement is met
 * @param isRequired Whether this is a required (vs optional) requirement
 */
@Composable
private fun RequirementItem(
    text: String,
    isMet: Boolean,
    isRequired: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor = when {
        isMet -> SafetyGreen
        isRequired -> AlertRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val textColor = when {
        isMet -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val iconChar = if (isMet) "✓" else "○"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$text: ${if (isMet) "met" else "not met"}"
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = iconChar,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = iconColor,
            modifier = Modifier.width(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/**
 * Compact version for smaller spaces.
 */
@Composable
fun CompactPasswordRequirementsChecklist(
    password: String,
    confirmPassword: String,
    modifier: Modifier = Modifier
) {
    val requirements = PasswordRequirements(
        hasMinLength = password.length >= 8,
        hasUppercase = password.any { it.isUpperCase() },
        hasLowercase = password.any { it.isLowerCase() },
        hasDigit = password.any { it.isDigit() },
        hasSpecialChar = password.any { !it.isLetterOrDigit() }
    )

    val passwordsMatch = password.isNotEmpty() &&
            confirmPassword.isNotEmpty() &&
            password == confirmPassword

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CompactRequirementItem("8+ chars", requirements.hasMinLength)
        CompactRequirementItem("A-Z", requirements.hasUppercase)
        CompactRequirementItem("a-z", requirements.hasLowercase)
        CompactRequirementItem("0-9", requirements.hasDigit)
        if (confirmPassword.isNotEmpty()) {
            CompactRequirementItem("Match", passwordsMatch)
        }
    }
}

@Composable
private fun CompactRequirementItem(
    text: String,
    isMet: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isMet) SafetyGreen else MaterialTheme.colorScheme.onSurfaceVariant
    val iconChar = if (isMet) "✓" else "○"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = iconChar,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color,
            modifier = Modifier.width(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}