package net.hitpromo.hitpromoworkstation.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Industrial Design System Dimensions
 *
 * Optimized for Samsung Galaxy Tab A9+ (11" landscape, 1920x1200)
 * - Physical dimensions: 254.2 x 165.8 x 6.9 mm
 * - Screen density: ~213 PPI
 * - Minimum touch target: 48dp (9mm physical)
 * - Preferred touch target: 64dp (12mm physical) for gloved operation
 */

object IndustrialDimensions {

    // Screen specifications for Samsung Galaxy Tab A9+
    val screenWidth = 1920.dp
    val screenHeight = 1200.dp
    val screenDensity = 213 // PPI

    // Touch Target Sizes - Critical for gloved operation
    val touchTargetMinimum = 48.dp      // WCAG minimum
    val touchTargetPreferred = 64.dp    // Industrial preference
    val touchTargetLarge = 72.dp        // Emergency/critical actions
    val touchTargetSpacing = 8.dp       // Minimum spacing between targets

    // Layout Spacing System
    val spacingXxs = 4.dp               // Micro spacing
    val spacingXs = 8.dp                // Small spacing
    val spacingSm = 12.dp               // Medium-small spacing
    val spacingMd = 16.dp               // Standard spacing
    val spacingLg = 24.dp               // Large spacing
    val spacingXl = 32.dp               // Extra large spacing
    val spacingXxl = 48.dp              // Maximum spacing
    val spacingXxxl = 64.dp             // Section breaks

    // Container Dimensions
    val cardMinHeight = 120.dp          // Minimum card height
    val cardPadding = 20.dp             // Standard card padding
    val cardPaddingLarge = 32.dp        // Large card padding
    val cardElevation = 8.dp            // Standard elevation
    val cardElevationHigh = 12.dp       // High elevation for focus

    // Form Elements
    val textFieldHeight = 64.dp         // Standard text field height
    val textFieldHeightLarge = 72.dp    // Large text field height
    val buttonHeight = 64.dp            // Standard button height
    val buttonHeightLarge = 72.dp       // Large button height
    val buttonPaddingHorizontal = 32.dp // Horizontal button padding
    val buttonPaddingVertical = 16.dp   // Vertical button padding

    // Border and Corner Radius
    val borderWidthThin = 1.dp          // Thin borders
    val borderWidthStandard = 2.dp      // Standard borders
    val borderWidthThick = 3.dp         // Thick borders (focus indicators)
    val cornerRadiusSmall = 8.dp        // Small corners
    val cornerRadiusStandard = 12.dp    // Standard corners
    val cornerRadiusLarge = 16.dp       // Large corners
    val cornerRadiusXLarge = 24.dp      // Extra large corners

    // Navigation and Header
    val topBarHeight = 80.dp            // Top navigation bar height
    val bottomBarHeight = 80.dp         // Bottom navigation bar height
    val sideBarWidth = 320.dp           // Side navigation width
    val headerHeight = 120.dp           // Page header height

    // Content Areas
    val contentMaxWidth = 1600.dp       // Maximum content width
    val contentPadding = 24.dp          // Standard content padding
    val contentPaddingLarge = 48.dp     // Large content padding
    val sectionSpacing = 40.dp          // Between major sections

    // Modal and Dialog
    val dialogMinWidth = 400.dp         // Minimum dialog width
    val dialogMaxWidth = 800.dp         // Maximum dialog width
    val dialogPadding = 32.dp           // Dialog content padding
    val modalOverlayAlpha = 0.6f        // Modal overlay transparency

    // List and Grid Items
    val listItemHeight = 80.dp          // Standard list item height
    val listItemHeightLarge = 96.dp     // Large list item height
    val gridItemMinSize = 160.dp        // Minimum grid item size
    val gridSpacing = 16.dp             // Grid item spacing

    // Icon Sizes
    val iconSizeSmall = 20.dp           // Small icons
    val iconSizeStandard = 24.dp        // Standard icons
    val iconSizeLarge = 32.dp           // Large icons
    val iconSizeXLarge = 48.dp          // Extra large icons

    // Focus and Selection Indicators
    val focusIndicatorWidth = 4.dp      // Focus ring width
    val selectionIndicatorWidth = 3.dp  // Selection indicator width
    val rippleRadius = 32.dp            // Touch ripple radius

    // Progress and Loading
    val progressBarHeight = 8.dp        // Progress bar height
    val progressBarHeightLarge = 12.dp  // Large progress bar height
    val loadingIndicatorSize = 48.dp    // Loading spinner size
    val loadingIndicatorSizeLarge = 64.dp // Large loading spinner

    // Tablet Landscape Specific Measurements
    val landscapeContentSplit = 0.6f    // Content area ratio (60/40 split)
    val landscapeSidebarRatio = 0.3f    // Sidebar ratio
    val landscapeHeaderRatio = 0.08f    // Header height ratio
    val landscapeFooterRatio = 0.06f    // Footer height ratio

    // Safe Areas and Margins
    val systemBarHeight = 24.dp         // System status bar
    val navigationBarHeight = 48.dp     // System navigation bar
    val safeAreaPadding = 16.dp         // Safe area padding
    val edgePadding = 24.dp             // Screen edge padding

    // Accessibility Enhancements
    val largeTextMultiplier = 1.2f      // Large text scaling
    val highContrastBorderWidth = 3.dp  // High contrast mode borders
    val minimumTapArea = 44.dp          // iOS accessibility minimum
    val recommendedTapArea = 48.dp      // Android accessibility minimum

    // Animation and Transition
    val animationDurationFast = 150     // Fast animations (ms)
    val animationDurationStandard = 300 // Standard animations (ms)
    val animationDurationSlow = 500     // Slow animations (ms)
}

/**
 * Layout Constants for Different Screen Sections
 */
object LayoutConstants {
    // Login Screen Layout
    const val LOGIN_BRANDING_WEIGHT = 0.4f     // Left column weight
    const val LOGIN_FORM_WEIGHT = 0.6f         // Right column weight
    const val LOGIN_FORM_MAX_WIDTH = 600       // Maximum form width (dp)

    // Dashboard Layout
    const val DASHBOARD_SIDEBAR_WEIGHT = 0.25f // Sidebar weight
    const val DASHBOARD_CONTENT_WEIGHT = 0.75f // Main content weight

    // Production Floor Layout
    const val FLOOR_CONTROL_HEIGHT = 120       // Control panel height (dp)
    const val FLOOR_STATUS_WIDTH = 300         // Status panel width (dp)
    const val FLOOR_VIDEO_ASPECT = 16f/9f      // Video aspect ratio

    // Tablet Orientation Breakpoints
    const val LANDSCAPE_MIN_WIDTH = 900        // Minimum width for landscape (dp)
    const val PORTRAIT_MAX_WIDTH = 600         // Maximum width for portrait (dp)

    // Grid Layouts
    const val GRID_COLUMNS_SMALL = 2           // Small screen grid columns
    const val GRID_COLUMNS_MEDIUM = 3          // Medium screen grid columns
    const val GRID_COLUMNS_LARGE = 4           // Large screen grid columns

    // Content Containers
    const val MAX_CONTENT_WIDTH = 1400         // Maximum content container (dp)
    const val FORM_MAX_WIDTH = 500             // Maximum form width (dp)
    const val CARD_MIN_HEIGHT = 120            // Minimum card height (dp)
}