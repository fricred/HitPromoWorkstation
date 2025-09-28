package net.hitpromo.hitpromoworkstation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.hitpromo.hitpromoworkstation.ui.theme.*

/**
 * Industrial UI Components Library
 *
 * Designed for Samsung Galaxy Tab A9+ in production floor environment:
 * - Large touch targets (minimum 48dp) for gloved operation
 * - High contrast colors for bright lighting conditions
 * - Clear visual feedback for all interactive states
 * - Accessibility-first design with proper semantics
 * - Consistent spacing and typography throughout
 */

/**
 * Industrial Button - Primary action button with large touch targets
 */
@Composable
fun IndustrialButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(
        defaultElevation = 6.dp,
        pressedElevation = 12.dp,
        disabledElevation = 0.dp
    ),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 32.dp,
        vertical = 16.dp
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 48.dp) // Minimum touch target size
            .semantics {
                contentDescription = "Industrial action button"
            },
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * Industrial Secondary Button - For secondary actions
 */
@Composable
fun IndustrialSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics {
                contentDescription = "Industrial secondary button"
            },
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (enabled) MaterialTheme.colorScheme.primary
                   else MaterialTheme.colorScheme.surfaceVariant
        ),
        contentPadding = PaddingValues(
            horizontal = 32.dp,
            vertical = 16.dp
        ),
        content = content
    )
}

/**
 * Industrial Text Field - Enhanced input field for production environment
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndustrialTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        errorContainerColor = MaterialTheme.colorScheme.errorContainer,

        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
        errorIndicatorColor = MaterialTheme.colorScheme.error,

        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor = MaterialTheme.colorScheme.error,

        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorTextColor = MaterialTheme.colorScheme.onErrorContainer
    )
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .heightIn(min = 64.dp) // Large touch target
            .semantics {
                contentDescription = "Industrial text input field"
            },
        enabled = enabled,
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodyLarge,
        label = label?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        placeholder = placeholder?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        colors = colors
    )
}

/**
 * Industrial Card - Container for content sections
 */
@Composable
fun IndustrialCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    elevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = 8.dp
    ),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .semantics {
                contentDescription = "Industrial content card"
            },
        shape = RoundedCornerShape(16.dp),
        colors = colors,
        elevation = elevation,
        border = border,
        content = content
    )
}

/**
 * Industrial Status Badge - For displaying status information
 */
@Composable
fun IndustrialStatusBadge(
    text: String,
    status: StatusType,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        StatusType.SUCCESS -> SafetyGreen
        StatusType.WARNING -> WarningAmber
        StatusType.ERROR -> AlertRed
        StatusType.INFO -> InfoBlue
        StatusType.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier
            .semantics {
                contentDescription = "Status indicator: $text"
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = TextOnColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Industrial Toggle Button - For binary state selections
 */
@Composable
fun IndustrialToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    icon: ImageVector? = null
) {
    val backgroundColor = if (checked) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (checked) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics {
                contentDescription = "Toggle: $text, ${if (checked) "enabled" else "disabled"}"
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (checked) 8.dp else 2.dp
        ),
        onClick = { if (enabled) onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = contentColor
            )
        }
    }
}

/**
 * Industrial Loading Indicator - For async operations
 */
@Composable
fun IndustrialLoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Loading...",
    size: LoadingSize = LoadingSize.MEDIUM
) {
    val indicatorSize = when (size) {
        LoadingSize.SMALL -> 32.dp
        LoadingSize.MEDIUM -> 48.dp
        LoadingSize.LARGE -> 64.dp
    }

    Column(
        modifier = modifier
            .semantics {
                contentDescription = "Loading: $message"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(indicatorSize),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

// Enums for component configuration
enum class StatusType {
    SUCCESS, WARNING, ERROR, INFO, NEUTRAL
}

enum class LoadingSize {
    SMALL, MEDIUM, LARGE
}

// Preview Composables
@Preview(showBackground = true)
@Composable
fun IndustrialComponentsPreview() {
    HitPromoWorkstationTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IndustrialButton(
                onClick = { }
            ) {
                Text("Primary Button")
            }

            IndustrialSecondaryButton(
                onClick = { }
            ) {
                Text("Secondary Button")
            }

            IndustrialTextField(
                value = "Sample text",
                onValueChange = { },
                label = "Label",
                placeholder = "Placeholder text"
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IndustrialStatusBadge("Success", StatusType.SUCCESS)
                IndustrialStatusBadge("Warning", StatusType.WARNING)
                IndustrialStatusBadge("Error", StatusType.ERROR)
                IndustrialStatusBadge("Info", StatusType.INFO)
            }

            IndustrialToggleButton(
                checked = true,
                onCheckedChange = { },
                text = "Toggle Option"
            )

            IndustrialLoadingIndicator(
                message = "Processing...",
                size = LoadingSize.MEDIUM
            )
        }
    }
}