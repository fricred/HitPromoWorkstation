package net.hitpromo.hitpromoworkstation.ui.screens

import android.content.res.Configuration
import android.view.KeyEvent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.hitpromo.hitpromoworkstation.R
import net.hitpromo.hitpromoworkstation.presentation.components.IndustrialButton
import net.hitpromo.hitpromoworkstation.presentation.components.IndustrialStatusText
import net.hitpromo.hitpromoworkstation.presentation.login.DebugLog
import net.hitpromo.hitpromoworkstation.presentation.login.ScannerStatus
import net.hitpromo.hitpromoworkstation.ui.components.DebugLogsModal
import net.hitpromo.hitpromoworkstation.ui.theme.AlertRed
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme
import net.hitpromo.hitpromoworkstation.ui.theme.InfoBlue
import net.hitpromo.hitpromoworkstation.ui.theme.SafetyGreen
import net.hitpromo.hitpromoworkstation.ui.theme.WarningAmber

/**
 * Badge Scanning Login Screen for Hit Promotional Products Workstation
 *
 * Optimized for Samsung Galaxy Tab A9+ (11" landscape) with Zebra LS2208 barcode scanner:
 * - Zebra LS2208 acts as keyboard wedge (types badge ID + Enter)
 * - Large touch targets for gloved operation
 * - High contrast colors for production floor lighting
 * - Clean, centered design with prominent logo
 * - Simple one-button interface
 * - Scanner status display and debug logs for troubleshooting
 */
@Composable
fun LoginScreen(
    onBadgeScan: (badgeId: String) -> Unit,
    onReadyToScan: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isReadyToScan: Boolean = false,
    errorMessage: String? = null,
    scannerStatus: ScannerStatus = ScannerStatus.Initializing,
    scannerName: String? = null,
    debugLogs: List<DebugLog> = emptyList(),
    showDebugModal: Boolean = false,
    onToggleDebugLogs: () -> Unit = {},
    onCopyLogs: () -> Unit = {},
    onClearLogs: () -> Unit = {}
) {
    var scannedInput by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Focus the invisible text field when ready to scan
    LaunchedEffect(isReadyToScan) {
        if (isReadyToScan) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Logo in top-left corner
        Image(
            painter = painterResource(id = R.drawable.hit_promo_logo),
            contentDescription = "Hit Promotional Products Logo",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(48.dp)
                .size(180.dp)
        )

        // Debug logs button in top-right corner
        IconButton(
            onClick = onToggleDebugLogs,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(48.dp)
                .size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Debug Logs",
                tint = if (debugLogs.isNotEmpty()) InfoBlue else Color.Gray,
                modifier = Modifier.size(48.dp)
            )
        }

        // Centered content area
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Status message or error
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Authenticating...",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else if (isReadyToScan) {
                Text(
                    text = "Ready to scan...",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            } else if (errorMessage != null) {
                IndustrialStatusText(
                    text = errorMessage,
                    isError = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main action button - "Scan ID to Login"
            IndustrialButton(
                text = "Scan ID to Login",
                onClick = {
                    scannedInput = ""
                    onReadyToScan()
                },
                isLoading = isLoading,
                enabled = !isLoading && !isReadyToScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
        }

        // Invisible text field to capture scanner input
        // The Zebra LS2208 scanner acts as a keyboard wedge
        TextField(
            value = scannedInput,
            onValueChange = { scannedInput = it },
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester)
                .onKeyEvent { keyEvent ->
                    // Detect Enter key press (scanner sends Enter after badge ID)
                    if (keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (scannedInput.isNotBlank()) {
                            onBadgeScan(scannedInput.trim())
                            scannedInput = ""
                        }
                        true
                    } else {
                        false
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (scannedInput.isNotBlank()) {
                        onBadgeScan(scannedInput.trim())
                        scannedInput = ""
                    }
                }
            )
        )

        // Scanner status display at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (statusText, statusColor) = when (scannerStatus) {
                ScannerStatus.Initializing -> "Initializing scanner..." to Color.Gray
                ScannerStatus.ScannerNotFound -> "Scanner not detected" to WarningAmber
                ScannerStatus.ScannerFound -> "Scanner detected: ${scannerName ?: "Unknown"}" to InfoBlue
                ScannerStatus.Connecting -> "Connecting to scanner..." to InfoBlue
                ScannerStatus.Connected -> "Scanner connected: ${scannerName ?: "Unknown"}" to SafetyGreen
                ScannerStatus.ReadyToScan -> "Ready to scan" to SafetyGreen
                ScannerStatus.Scanning -> "Scanning..." to SafetyGreen
                ScannerStatus.Disconnected -> "Scanner disconnected" to AlertRed
                ScannerStatus.Error -> "Scanner error" to AlertRed
            }

            Text(
                text = statusText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = statusColor
            )
        }

        // Debug logs modal
        if (showDebugModal) {
            DebugLogsModal(
                logs = debugLogs,
                onDismiss = onToggleDebugLogs,
                onCopyLogs = onCopyLogs,
                onClearLogs = onClearLogs
            )
        }
    }
}

@Preview(
    name = "Badge Scan Login - Landscape",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun BadgeScanLoginScreenPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onBadgeScan = { },
            onReadyToScan = { },
            isLoading = false,
            isReadyToScan = false,
            errorMessage = null
        )
    }
}

@Preview(
    name = "Badge Scan Login - Ready to Scan",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun BadgeScanLoginReadyPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onBadgeScan = { },
            onReadyToScan = { },
            isLoading = false,
            isReadyToScan = true,
            errorMessage = null
        )
    }
}

@Preview(
    name = "Badge Scan Login - Loading",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun BadgeScanLoginLoadingPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onBadgeScan = { },
            onReadyToScan = { },
            isLoading = true,
            isReadyToScan = false,
            errorMessage = null
        )
    }
}

@Preview(
    name = "Badge Scan Login - Error",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun BadgeScanLoginErrorPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onBadgeScan = { },
            onReadyToScan = { },
            isLoading = false,
            isReadyToScan = false,
            errorMessage = "Invalid badge ID. Please try again."
        )
    }
}
