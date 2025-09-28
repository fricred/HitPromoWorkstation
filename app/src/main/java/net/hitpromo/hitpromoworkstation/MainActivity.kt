package net.hitpromo.hitpromoworkstation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
 */
@Composable
fun IndustrialWorkstationApp() {
    // State management for authentication
    var isAuthenticated by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    if (!isAuthenticated) {
        LoginScreen(
            onLoginClick = { username, password ->
                // Login will be handled in the LoginScreen itself
                // For now, we'll do simple validation here
                when {
                    username.isBlank() -> loginError = "Username is required"
                    password.isBlank() -> loginError = "Password is required"
                    username.length < 3 -> loginError = "Username must be at least 3 characters"
                    password.length < 6 -> loginError = "Password must be at least 6 characters"
                    username == "demo" && password == "password" -> isAuthenticated = true
                    username == "admin" && password == "industrial123" -> isAuthenticated = true
                    else -> loginError = "Invalid username or password. Try demo/password or admin/industrial123"
                }
            },
            onForgotPasswordClick = {
                // TODO: Implement forgot password flow
                loginError = "Contact your system administrator for password reset"
            },
            isLoading = isLoading,
            errorMessage = loginError,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // TODO: Main application screens will be implemented here
        // For now, we'll show a placeholder
        DashboardPlaceholder()
    }
}

/**
 * Handle login authentication
 * TODO: Integrate with actual authentication service
 */
@Composable
private fun HandleLogin(
    username: String,
    password: String,
    onLoading: (Boolean) -> Unit,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(username, password) {
        onLoading(true)

        // Simulate authentication delay
        // TODO: Replace with actual authentication logic
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            onLoading(false)

            // Simple validation for demo purposes
            when {
                username.isBlank() -> onError("Username is required")
                password.isBlank() -> onError("Password is required")
                username.length < 3 -> onError("Username must be at least 3 characters")
                password.length < 6 -> onError("Password must be at least 6 characters")
                username == "demo" && password == "password" -> onSuccess()
                username == "admin" && password == "industrial123" -> onSuccess()
                else -> onError("Invalid username or password. Try demo/password or admin/industrial123")
            }
        }, 1500) // 1.5 second delay to simulate network request
    }
}

/**
 * Placeholder for main dashboard
 * TODO: Implement main application screens
 */
@Composable
fun DashboardPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
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
                text = "Authentication Successful - Main application screens coming soon",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}