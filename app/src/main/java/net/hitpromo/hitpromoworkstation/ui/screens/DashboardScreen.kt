package net.hitpromo.hitpromoworkstation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.hitpromo.hitpromoworkstation.domain.model.AnalyticsSummary
import net.hitpromo.hitpromoworkstation.domain.model.CameraSettings
import net.hitpromo.hitpromoworkstation.domain.model.DeviceInfo
import net.hitpromo.hitpromoworkstation.domain.model.StreamProtocol
import net.hitpromo.hitpromoworkstation.domain.model.StreamState
import net.hitpromo.hitpromoworkstation.domain.model.SystemHealth
import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.model.UserRole
import net.hitpromo.hitpromoworkstation.presentation.dashboard.DashboardIntent
import net.hitpromo.hitpromoworkstation.presentation.dashboard.DashboardUiState
import net.hitpromo.hitpromoworkstation.presentation.dashboard.DashboardViewModel
import net.hitpromo.hitpromoworkstation.ui.screens.dashboard.CameraPreviewSection
import net.hitpromo.hitpromoworkstation.ui.screens.dashboard.StreamControlSection
import net.hitpromo.hitpromoworkstation.ui.screens.dashboard.StreamStatusPanel
import net.hitpromo.hitpromoworkstation.ui.screens.dashboard.SystemMonitoringBar
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme

/**
 * Main Dashboard Screen for Hit Promotional Products Workstation
 *
 * Optimized for Samsung Galaxy Tab A9+ (11" landscape - 1920x1200):
 * - Two-column layout (55% camera / 45% status and controls)
 * - Large touch targets for gloved operation
 * - Real-time monitoring of stream, analytics, and system health
 * - Industrial design with high contrast for production floor visibility
 *
 * @param viewModel Dashboard ViewModel injected via Hilt
 * @param modifier Optional modifier for the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar for error and success messages
    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.handleIntent(DashboardIntent.ClearError)
        }
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.handleIntent(DashboardIntent.ClearError)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DashboardTopBar(
                deviceInfo = uiState.deviceInfo,
                onRefreshClick = { viewModel.handleIntent(DashboardIntent.RefreshAll) },
                onSettingsClick = { viewModel.handleIntent(DashboardIntent.OpenSettings) },
                onLogoutClick = { viewModel.handleIntent(DashboardIntent.SignOut) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            SystemMonitoringBar(
                systemHealth = uiState.systemHealth,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        DashboardContent(
            uiState = uiState,
            onIntent = { intent -> viewModel.handleIntent(intent) },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Top app bar with device info and action buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    deviceInfo: DeviceInfo,
    onRefreshClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.semantics {
            contentDescription = "Dashboard top bar with device info and controls"
        },
        title = {
            Column {
                Text(
                    text = "Hit Promo Workstation",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Device: ${deviceInfo.deviceId} • ${deviceInfo.location}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier.semantics {
                    contentDescription = "Refresh all dashboard data"
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.semantics {
                    contentDescription = "Open settings"
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier.semantics {
                    contentDescription = "Sign out"
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/**
 * Main dashboard content with two-column layout.
 */
@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onIntent: (DashboardIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Column: Camera Preview (55% width)
        Box(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight()
        ) {
            CameraPreviewSection(
                cameraSettings = uiState.cameraSettings,
                streamState = uiState.streamState,
                onToggleFlash = { onIntent(DashboardIntent.ToggleFlash) },
                onToggleAutoFocus = { onIntent(DashboardIntent.ToggleAutoFocus) },
                onOpenCameraSettings = { onIntent(DashboardIntent.OpenSettings) },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Right Column: Status, Controls, and Analytics (45% width)
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stream Status Panel
            StreamStatusPanel(
                streamState = uiState.streamState,
                streamQuality = uiState.streamQuality,
                currentProtocol = uiState.currentProtocol,
                analyticsSummary = uiState.analyticsSummary,
                streamUptime = uiState.formattedStreamUptime,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            )

            // Stream Control Buttons
            StreamControlSection(
                streamState = uiState.streamState,
                canStartStream = uiState.canStartStream,
                canStopStream = uiState.canStopStream,
                onStartStream = { protocol ->
                    onIntent(DashboardIntent.StartStream(protocol))
                },
                onStopStream = { onIntent(DashboardIntent.StopStream) },
                onEmergencyStop = { onIntent(DashboardIntent.StopStream) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ========================================
// Preview Composables
// ========================================

@Preview(
    name = "Dashboard - Idle State",
    widthDp = 1920,
    heightDp = 1200,
    showBackground = true
)
@Composable
fun DashboardScreenIdlePreview() {
    HitPromoWorkstationTheme {
        val previewState = DashboardUiState(
            user = User(
                id = "user_001",
                username = "operator_demo",
                email = "operator@hitpromo.net",
                role = UserRole.OPERATOR,
                isActive = true,
                lastLoginTime = System.currentTimeMillis()
            ),
            streamState = StreamState.Idle,
            streamQuality = null,
            cameraSettings = CameraSettings.Default,
            systemHealth = SystemHealth.Default,
            analyticsSummary = AnalyticsSummary.Empty,
            deviceInfo = DeviceInfo.getCurrentDevice()
        )

        Surface {
            DashboardContent(
                uiState = previewState,
                onIntent = {}
            )
        }
    }
}

@Preview(
    name = "Dashboard - Streaming",
    widthDp = 1920,
    heightDp = 1200,
    showBackground = true
)
@Composable
fun DashboardScreenStreamingPreview() {
    HitPromoWorkstationTheme {
        val previewState = DashboardUiState(
            user = User(
                id = "user_001",
                username = "operator_demo",
                email = "operator@hitpromo.net",
                role = UserRole.OPERATOR,
                isActive = true,
                lastLoginTime = System.currentTimeMillis()
            ),
            streamState = StreamState.Streaming(
                protocol = StreamProtocol.WEBRTC,
                quality = net.hitpromo.hitpromoworkstation.domain.model.StreamQuality.Default,
                startTime = System.currentTimeMillis() - 300000, // 5 minutes ago
                bytesTransferred = 157_286_400L
            ),
            streamQuality = net.hitpromo.hitpromoworkstation.domain.model.StreamQuality.Default,
            cameraSettings = CameraSettings.Default,
            systemHealth = SystemHealth.Default,
            analyticsSummary = AnalyticsSummary.Sample,
            deviceInfo = DeviceInfo.getCurrentDevice()
        )

        Surface {
            DashboardContent(
                uiState = previewState,
                onIntent = {}
            )
        }
    }
}
