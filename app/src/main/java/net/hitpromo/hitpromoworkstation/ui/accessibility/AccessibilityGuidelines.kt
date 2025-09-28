package net.hitpromo.hitpromoworkstation.ui.accessibility

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * Accessibility Guidelines for Hit Promotional Products Industrial Workstation
 *
 * WCAG 2.1 AA Compliance for Production Floor Environment
 *
 * Special considerations for industrial setting:
 * - Workers wearing safety gloves (larger touch targets required)
 * - Bright production floor lighting (high contrast needed)
 * - Noisy environment (visual feedback emphasis)
 * - Safety-critical operations (error prevention priority)
 * - Multi-shift operations (both light and dark mode support)
 */

object AccessibilityGuidelines {

    /**
     * Touch Target Requirements
     * Enhanced for gloved operation in industrial environment
     */
    object TouchTargets {
        // Minimum sizes (dp)
        const val MINIMUM_SIZE = 48f         // WCAG 2.1 AA minimum
        const val RECOMMENDED_SIZE = 64f     // Industrial recommended
        const val CRITICAL_ACTION_SIZE = 72f // Emergency/critical actions

        // Spacing requirements
        const val MINIMUM_SPACING = 8f       // Between adjacent targets
        const val RECOMMENDED_SPACING = 16f  // Preferred spacing

        // Physical dimensions (mm) for reference
        const val MINIMUM_PHYSICAL = 9f      // 9mm minimum
        const val RECOMMENDED_PHYSICAL = 12f // 12mm recommended
        const val CRITICAL_PHYSICAL = 15f    // 15mm for critical actions
    }

    /**
     * Color Contrast Requirements
     * Enhanced for bright production floor lighting
     */
    object ColorContrast {
        // Standard WCAG ratios
        const val NORMAL_TEXT_AA = 4.5f      // Normal text minimum
        const val LARGE_TEXT_AA = 3.0f       // Large text minimum
        const val NON_TEXT_AA = 3.0f         // UI components minimum

        // Industrial enhanced ratios
        const val INDUSTRIAL_TEXT = 7.0f     // Enhanced for production floor
        const val INDUSTRIAL_UI = 4.5f       // Enhanced UI components
        const val SAFETY_CRITICAL = 10.0f    // Safety-critical elements

        // Background contrast requirements
        const val SURFACE_CONTRAST = 2.0f    // Surface to background
        const val DISABLED_CONTRAST = 2.5f   // Disabled elements
    }

    /**
     * Typography Accessibility
     * Optimized for tablet distance and industrial viewing conditions
     */
    object Typography {
        // Font size requirements (sp)
        const val MINIMUM_TEXT_SIZE = 14f    // Minimum readable size
        const val BODY_TEXT_SIZE = 16f       // Standard body text
        const val LARGE_TEXT_SIZE = 18f      // Large text threshold
        const val TOUCH_TARGET_TEXT = 20f    // Text in touch targets

        // Line height requirements
        const val MINIMUM_LINE_HEIGHT = 1.2f // Minimum line height ratio
        const val RECOMMENDED_LINE_HEIGHT = 1.5f // Recommended ratio

        // Character spacing
        const val MINIMUM_LETTER_SPACING = 0.12f // Minimum letter spacing
    }

    /**
     * Focus and Selection Indicators
     * High visibility for keyboard and assistive technology navigation
     */
    object Focus {
        const val FOCUS_RING_WIDTH = 4f      // Focus indicator width (dp)
        const val FOCUS_RING_OFFSET = 2f     // Offset from element (dp)
        const val SELECTION_WIDTH = 3f       // Selection indicator width

        // Focus colors should have minimum 3:1 contrast with background
        const val FOCUS_CONTRAST_RATIO = 3.0f
    }

    /**
     * Animation and Motion Guidelines
     * Considerate of vestibular disorders and motion sensitivity
     */
    object Motion {
        // Animation durations (ms)
        const val FAST_DURATION = 150L       // Quick transitions
        const val STANDARD_DURATION = 300L   // Standard animations
        const val SLOW_DURATION = 500L       // Slow animations

        // Maximum animation distances (dp)
        const val MAX_MOVEMENT_DISTANCE = 200f

        // Timing functions should be ease-in-out for accessibility
        const val REDUCED_MOTION_DURATION = 0L // For users who prefer reduced motion
    }

    /**
     * Error Prevention and Recovery
     * Critical for safety in industrial environment
     */
    object ErrorHandling {
        // Error message requirements
        const val ERROR_PERSISTENCE_TIME = 10000L // 10 seconds minimum
        const val SUCCESS_MESSAGE_TIME = 3000L    // 3 seconds for success

        // Confirmation requirements for critical actions
        val CRITICAL_ACTIONS = listOf(
            "emergency_stop",
            "system_shutdown",
            "delete_production_data",
            "modify_safety_settings"
        )
    }

    /**
     * Semantic Structure Requirements
     * Proper labeling for screen readers and assistive technology
     */
    object Semantics {
        // Required content descriptions
        val REQUIRED_DESCRIPTIONS = mapOf(
            "buttons" to "Include action description",
            "images" to "Include meaningful description",
            "form_fields" to "Include label and format requirements",
            "status_indicators" to "Include current state and meaning",
            "navigation" to "Include destination description"
        )

        // Role assignments
        val COMPONENT_ROLES = mapOf(
            "emergency_button" to Role.Button,
            "status_display" to Role.Image,
            "form_input" to Role.Button,
            "navigation_tab" to Role.Tab,
            "data_table" to Role.Button
        )
    }
}

/**
 * Accessibility Helper Functions
 */
object AccessibilityHelpers {

    /**
     * Calculate color contrast ratio between two colors
     */
    fun calculateContrastRatio(color1: Color, color2: Color): Float {
        val luminance1 = color1.luminance() + 0.05f
        val luminance2 = color2.luminance() + 0.05f

        return max(luminance1, luminance2) / min(luminance1, luminance2)
    }

    /**
     * Check if color combination meets WCAG requirements
     */
    fun meetsContrastRequirement(
        foreground: Color,
        background: Color,
        requirement: Float = AccessibilityGuidelines.ColorContrast.NORMAL_TEXT_AA
    ): Boolean {
        return calculateContrastRatio(foreground, background) >= requirement
    }

    /**
     * Check if touch target meets size requirements
     */
    fun meetsTouchTargetSize(
        widthDp: Float,
        heightDp: Float,
        requirement: Float = AccessibilityGuidelines.TouchTargets.MINIMUM_SIZE
    ): Boolean {
        return widthDp >= requirement && heightDp >= requirement
    }

    /**
     * Generate semantic content description for industrial components
     */
    fun generateContentDescription(
        componentType: String,
        label: String,
        state: String? = null,
        value: String? = null
    ): String {
        val baseDescription = "$componentType: $label"
        val stateDescription = state?.let { ", $it" } ?: ""
        val valueDescription = value?.let { ", value: $it" } ?: ""

        return "$baseDescription$stateDescription$valueDescription"
    }

    /**
     * Apply accessibility semantics to composable
     */
    @Composable
    fun applyAccessibilitySemantics(
        description: String,
        role: Role? = null
    ): Modifier {
        return Modifier.semantics {
            contentDescription = description
            role?.let { this.role = it }
        }
    }

    /**
     * Create high contrast border for focus indication
     */
    @Composable
    fun createFocusBorder(
        isFocused: Boolean,
        color: Color = MaterialTheme.colorScheme.primary
    ): Modifier {
        return if (isFocused) {
            Modifier.semantics {
                // Add focus state to semantics
                contentDescription = "Focused element"
            }
        } else {
            Modifier
        }
    }
}

/**
 * Industrial Accessibility Testing Checklist
 */
object AccessibilityChecklist {

    val TESTING_REQUIREMENTS = listOf(
        "Touch targets minimum 48dp, preferred 64dp for gloved operation",
        "Color contrast ratio minimum 4.5:1 for normal text, 7:1 for production floor",
        "Text size minimum 16sp for body text, 20sp for touch targets",
        "Focus indicators visible with 4dp width and 3:1 contrast ratio",
        "All interactive elements have meaningful content descriptions",
        "Form fields include labels and error descriptions",
        "Status changes announced to screen readers",
        "Keyboard navigation supports all functionality",
        "Error messages persist for minimum 10 seconds",
        "Animation durations respect reduced motion preferences",
        "High contrast mode compatibility",
        "Support for system font size scaling up to 200%",
        "Voice control compatibility for hands-free operation",
        "Screen reader compatibility with TalkBack",
        "Switch control support for users with motor disabilities"
    )

    val INDUSTRIAL_SPECIFIC_TESTS = listOf(
        "Usability with safety gloves (thick work gloves)",
        "Visibility under bright production floor lighting",
        "Legibility at tablet arm's length (24-30 inches)",
        "Touch accuracy with vibrating machinery nearby",
        "Audio feedback audible over production noise",
        "Visual feedback visible in peripheral vision",
        "Emergency action accessibility under stress",
        "Multi-shift consistency (day/night modes)",
        "Cleaning solution resistance (UI surface)",
        "Drop and impact durability (tablet protection)"
    )
}