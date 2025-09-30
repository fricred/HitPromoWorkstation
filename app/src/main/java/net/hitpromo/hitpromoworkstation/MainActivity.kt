package net.hitpromo.hitpromoworkstation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import net.hitpromo.hitpromoworkstation.presentation.login.LoginIntent
import net.hitpromo.hitpromoworkstation.presentation.login.LoginViewModel
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialSecondaryButton
import net.hitpromo.hitpromoworkstation.ui.screens.ForgotPasswordScreen
import net.hitpromo.hitpromoworkstation.ui.screens.ForcePasswordChangeScreen
import net.hitpromo.hitpromoworkstation.ui.screens.LoginScreen
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme

/**
 * Main Activity for Hit Promotional Products Industrial Workstation
 *
 * Configured for Samsung Galaxy Tab A9+ (11" landscape):
 * - Forced landscape orientation for production floor use
 * - Edge-to-edge display for maximum screen real estate
 * - Industrial theme with high contrast colors
 * - Accessibility optimizations for gloved operation
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure window for edge-to-edge display
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            HitPromoWorkstationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    IndustrialWorkstationApp()
                }
            }
        }
    }
}

/**
 * Main application composable
 * Manages navigation between login and main application screens
 * Integrates with LoginViewModel for AWS Cognito authentication
 */
@Composable
fun IndustrialWorkstationApp(
    viewModel: LoginViewModel = hiltViewModel()
) {
    // Check Amplify initialization status before proceeding
    if (!HitPromoWorkstationApplication.isAmplifyConfigured()) {
        AmplifyErrorScreen(
            error = HitPromoWorkstationApplication.getAmplifyInitializationError()
        )
        return
    }

    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Local navigation state for forgot password flow
    var showForgotPassword by remember { mutableStateOf(false) }

    when {
        // Show loading screen during initial session check
        !uiState.isSessionValidated && uiState.isLoading -> {
            LoadingScreen(modifier = Modifier.fillMaxSize())
        }
        // Show password change screen if required
        uiState.requirePasswordChange && uiState.passwordChangeUsername != null && uiState.passwordChangeSessionId != null -> {
            ForcePasswordChangeScreen(
                username = uiState.passwordChangeUsername!!,
                sessionId = uiState.passwordChangeSessionId!!,
                onPasswordChangeSuccess = {
                    // Password changed successfully, user is now authenticated
                    // Clear the password change flag to show authenticated state
                    viewModel.handleIntent(LoginIntent.ClearPasswordChangeState)
                },
                onCancel = {
                    // User cancelled password change, return to login
                    viewModel.handleIntent(LoginIntent.SignOut)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        // Show forgot password screen if requested
        showForgotPassword -> {
            ForgotPasswordScreen(
                onComplete = {
                    // Return to login screen after successful password reset
                    showForgotPassword = false
                },
                onCancel = {
                    // User cancelled forgot password flow, return to login
                    showForgotPassword = false
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        // Show login screen if not authenticated
        !uiState.isAuthenticated -> {
            LoginScreen(
                onLoginClick = { username, password ->
                    // Trigger sign-in through ViewModel
                    viewModel.handleIntent(LoginIntent.SignIn(username, password))
                },
                onForgotPasswordClick = {
                    // Navigate to forgot password screen
                    showForgotPassword = true
                },
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                modifier = Modifier.fillMaxSize()
            )
        }
        // Show dashboard if authenticated
        else -> {
            DashboardPlaceholder(
                user = uiState.user,
                viewModel = viewModel
            )
        }
    }
}

/**
 * Loading screen shown during session validation
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Company Logo Placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HP",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hit Promotional Products",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Validating session...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Placeholder for main dashboard
 * TODO: Implement main application screens
 */
@Composable
fun DashboardPlaceholder(
    user: net.hitpromo.hitpromoworkstation.domain.model.User? = null,
    viewModel: LoginViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content centered
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hit Promotional Products",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Industrial Workstation Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Authentication Successful",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            user?.let {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome, ${it.username}!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Email: ${it.email}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Role: ${it.role.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Main application screens coming soon",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Logout button positioned at top-right
        IndustrialSecondaryButton(
            onClick = {
                viewModel.handleIntent(LoginIntent.SignOut)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
        ) {
            Text("Logout")
        }
    }
}

/**
 * Error screen displayed when Amplify initialization fails.
 * Provides diagnostic information for configuration issues.
 */
@Composable
fun AmplifyErrorScreen(
    error: Throwable?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Error Icon
            Surface(
                modifier = Modifier.size(80.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.error
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "!",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Configuration Error",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AWS Amplify failed to initialize",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error details card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error Details:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = error?.message ?: "Unknown configuration error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Required Action:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Ensure amplifyconfiguration.json exists in app/src/main/res/raw/\n" +
                                "• Verify the file contains valid JSON configuration\n" +
                                "• Check AWS Cognito credentials and region settings\n" +
                                "• Rebuild and reinstall the application",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Contact your system administrator for assistance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}