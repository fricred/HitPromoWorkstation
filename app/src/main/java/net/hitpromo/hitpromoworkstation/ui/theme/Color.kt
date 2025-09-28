package net.hitpromo.hitpromoworkstation.ui.theme

import androidx.compose.ui.graphics.Color

// Industrial Color Palette - High Contrast for Production Floor
// Primary Colors - Hit Promo Brand with Industrial Enhancement
val HitPromoOrange = Color(0xFFFF6600)      // Brand orange - high visibility
val HitPromoOrangeDark = Color(0xFFE55A00)  // Darker variant for dark mode
val HitPromoBlue = Color(0xFF003D7A)        // Professional blue - readable on screens

// Industrial Background Colors - Optimized for bright production lighting
val IndustrialWhite = Color(0xFFFAFAFA)     // Slightly off-white to reduce glare
val IndustrialDark = Color(0xFF1A1A1A)      // Deep dark for high contrast
val IndustrialGray = Color(0xFF2C2C2C)      // Medium dark for cards/surfaces

// Functional Colors - High Contrast for Safety and Clarity
val SafetyGreen = Color(0xFF00C851)         // Success/safe states
val AlertRed = Color(0xFFFF4444)           // Errors/alerts - highly visible
val WarningAmber = Color(0xFFFFBB33)       // Warnings - attention grabbing
val InfoBlue = Color(0xFF0099FF)           // Information states

// Text Colors - Maximum Readability
val TextPrimary = Color(0xFF000000)        // Pure black for maximum contrast
val TextSecondary = Color(0xFF424242)      // Dark gray for secondary text
val TextOnDark = Color(0xFFFFFFFF)         // Pure white on dark backgrounds
val TextOnColor = Color(0xFFFFFFFF)        // White text on colored backgrounds

// Surface Colors - Production Environment Optimized
val SurfacePrimary = Color(0xFFFFFFFF)     // Primary surface - clean white
val SurfaceSecondary = Color(0xFFF5F5F5)   // Secondary surface - light gray
val SurfaceDark = Color(0xFF1E1E1E)        // Dark surface for dark mode
val SurfaceElevated = Color(0xFFFFFFFF)    // Elevated surfaces (cards, dialogs)

// Border and Divider Colors
val BorderLight = Color(0xFFE0E0E0)        // Light borders and dividers
val BorderDark = Color(0xFF424242)         // Dark borders for contrast
val BorderAccent = Color(0xFFFF6600)       // Accent borders for emphasis

// Legacy colors for compatibility (to be replaced)
val Purple80 = HitPromoOrangeDark
val PurpleGrey80 = IndustrialGray
val Pink80 = WarningAmber

val Purple40 = HitPromoOrange
val PurpleGrey40 = IndustrialDark
val Pink40 = AlertRed