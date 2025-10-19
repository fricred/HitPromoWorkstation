package net.hitpromo.hitpromoworkstation.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalConfiguration
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
 * Form-based Login Screen for Hit Promotional Products Workstation
 *
 * Optimized for Samsung Galaxy Tab A9+ (11" landscape) with form input:
 * - Badge ID input field (supports keyboard wedge scanner input)
 * - Machine ID input field
 * - Large touch targets for gloved operation
 * - High contrast colors for production floor lighting
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
    onClearLogs: () -> Unit = {},
    badgeId: String = "",
    onBadgeIdChange: (String) -> Unit = {},
    machineId: String = "",
    onMachineIdChange: (String) -> Unit = {},
    onSubmitForm: (badgeId: String, machineId: String) -> Unit = { _, _ -> },
    isWorkersLoginInProgress: Boolean = false,
    onRetryWorkersLogin: () -> Unit = {}
) {
    var scannedInput by remember { mutableStateOf("") }
    val badgeIdFocusRequester = remember { FocusRequester() }
    val machineIdFocusRequester = remember { FocusRequester() }
    val scannerFocusRequester = remember { FocusRequester() }

    // Get screen dimensions for responsive layout
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Responsive sizing
    val isTablet = screenWidth >= 600.dp

    val logoPadding = if (isTablet) 32.dp else 16.dp
    val logoSize = if (isTablet) 140.dp else 80.dp
    val debugButtonPadding = if (isTablet) 32.dp else 16.dp
    val debugButtonSize = if (isTablet) 56.dp else 48.dp
    val debugIconSize = if (isTablet) 40.dp else 32.dp
    val contentWidth = if (isTablet) 500.dp else screenWidth * 0.85f
    val buttonHeight = if (isTablet) 56.dp else 48.dp
    val fieldSpacing = if (isTablet) 20.dp else 16.dp
    val statusBarPadding = if (isTablet) 24.dp else 16.dp
    val statusTextSize = if (isTablet) 14.sp else 12.sp

    // Focus badge ID field when ready to scan
    LaunchedEffect(isReadyToScan) {
        if (isReadyToScan) {
            scannerFocusRequester.requestFocus()
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
                .padding(logoPadding)
                .size(logoSize)
        )

        // Debug logs button in top-right corner
        IconButton(
            onClick = onToggleDebugLogs,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(debugButtonPadding)
                .size(debugButtonSize)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Debug Logs",
                tint = if (debugLogs.isNotEmpty()) InfoBlue else Color.Gray,
                modifier = Modifier.size(debugIconSize)
            )
        }

        // Centered form area inside Card
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .width(contentWidth)
                .padding(horizontal = if (isTablet) 0.dp else 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main title
                Text(
                    text = "Login Information",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = if (isTablet) 28.sp else 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Subtitle
                Text(
                    text = "Enter your badge and machine details",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Error message
                if (errorMessage != null) {
                    IndustrialStatusText(
                        text = errorMessage,
                        isError = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                // Badge ID label and input field
                Text(
                    text = "Scan or enter your BadgeID",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                TextField(
                    value = badgeId,
                    onValueChange = onBadgeIdChange,
                    placeholder = { Text("Enter Badge ID", color = Color.Gray) },
                    singleLine = true,
                    enabled = !isLoading && !isWorkersLoginInProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .focusRequester(badgeIdFocusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = if (isTablet) 16.sp else 14.sp
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { machineIdFocusRequester.requestFocus() }
                    )
                )

                Spacer(modifier = Modifier.height(fieldSpacing))

                // Machine ID label and input field
                Text(
                    text = "Enter the Machine ID",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                TextField(
                    value = machineId,
                    onValueChange = onMachineIdChange,
                    placeholder = { Text("Enter Machine ID", color = Color.Gray) },
                    singleLine = true,
                    enabled = !isLoading && !isWorkersLoginInProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                        .focusRequester(machineIdFocusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = if (isTablet) 16.sp else 14.sp
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (badgeId.isNotBlank() && machineId.isNotBlank()) {
                                onSubmitForm(badgeId, machineId)
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Loading state indicator
                if (isLoading || isWorkersLoginInProgress) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isWorkersLoginInProgress) "Registering worker..." else "Authenticating...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Submit button
                IndustrialButton(
                    text = "Submit",
                    onClick = { onSubmitForm(badgeId, machineId) },
                    isLoading = isLoading || isWorkersLoginInProgress,
                    enabled = !isLoading && !isWorkersLoginInProgress && badgeId.isNotBlank() && machineId.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight)
                )

                // Retry button (shown when workers login fails)
                if (!isLoading && isWorkersLoginInProgress && errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    IndustrialButton(
                        text = "Retry",
                        onClick = onRetryWorkersLogin,
                        isLoading = false,
                        enabled = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(buttonHeight)
                    )
                }
            }
        }

        // Invisible text field to capture scanner input for badge ID field
        TextField(
            value = scannedInput,
            onValueChange = { scannedInput = it },
            modifier = Modifier
                .size(1.dp)
                .focusRequester(scannerFocusRequester)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (scannedInput.isNotBlank()) {
                            onBadgeIdChange(scannedInput.trim())
                            scannedInput = ""
                            machineIdFocusRequester.requestFocus()
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (scannedInput.isNotBlank()) {
                        onBadgeIdChange(scannedInput.trim())
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
                .padding(statusBarPadding),
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
                fontSize = statusTextSize,
                fontWeight = FontWeight.Medium,
                color = statusColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
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
    name = "Form Login - Landscape",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun FormLoginScreenPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onBadgeScan = { },
            onReadyToScan = { },
            isLoading = false,
            isReadyToScan = false,
            errorMessage = null,
            badgeId = "",
            machineId = ""
        )
    }
}

@Preview(
    name = "Form Login - Loading",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun FormLoginLoadingPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onBadgeScan = { },
            onReadyToScan = { },
            isLoading = true,
            isReadyToScan = false,
            errorMessage = null,
            badgeId = "A000004892",
            machineId = "M-001"
        )
    }
}

@Preview(
    name = "Form Login - Error",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun FormLoginErrorPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onBadgeScan = { },
            onReadyToScan = { },
            isLoading = false,
            isReadyToScan = false,
            errorMessage = "Badge authentication failed. Please try again.",
            badgeId = "A000004892",
            machineId = "M-001"
        )
    }
}

@Preview(
    name = "Form Login - Tablet Landscape",
    widthDp = 1024,
    heightDp = 768,
    showBackground = true
)
@Composable
fun FormLoginTabletPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onBadgeScan = { },
            onReadyToScan = { },
            isLoading = false,
            isReadyToScan = false,
            errorMessage = null,
            badgeId = "",
            machineId = "",
            scannerStatus = ScannerStatus.Connected,
            scannerName = "LS2208"
        )
    }
}
