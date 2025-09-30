package net.hitpromo.hitpromoworkstation.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.hitpromo.hitpromoworkstation.presentation.components.IndustrialButton
import net.hitpromo.hitpromoworkstation.presentation.components.IndustrialCard
import net.hitpromo.hitpromoworkstation.presentation.components.IndustrialOutlinedButton
import net.hitpromo.hitpromoworkstation.presentation.components.IndustrialStatusText
import net.hitpromo.hitpromoworkstation.presentation.components.IndustrialTextField
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme
import net.hitpromo.hitpromoworkstation.ui.theme.IndustrialTextStyles

/**
 * Industrial Login Screen for Hit Promotional Products Workstation
 *
 * Optimized for Samsung Galaxy Tab A9+ (11" landscape):
 * - Large touch targets for gloved operation
 * - High contrast colors for production floor lighting
 * - Clear typography for maximum readability
 * - Professional branding and layout
 */
@Composable
fun LoginScreen(
    onLoginClick: (username: String, password: String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side - Branding and Information
            BrandingSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )

            // Right side - Login Form
            LoginFormSection(
                username = username,
                password = password,
                rememberMe = rememberMe,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onUsernameChange = { username = it },
                onPasswordChange = { password = it },
                onRememberMeChange = { rememberMe = it },
                onLoginClick = { onLoginClick(username, password) },
                onForgotPasswordClick = onForgotPasswordClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
        }
    }
}

/**
 * Left section with company branding and system information.
 */
@Composable
private fun BrandingSection(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Company Logo Placeholder
            // TODO: Replace with actual company logo
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

            Text(
                text = "Hit Promotional Products",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Industrial Workstation",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            IndustrialCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "System Information",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SystemInfoRow("Version", "1.0.0")
                    SystemInfoRow("Environment", "Production")
                    SystemInfoRow("Device", "Samsung Galaxy Tab A9+")
                    SystemInfoRow("Orientation", "Landscape")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "For technical support, contact your system administrator",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Right section with the login form.
 */
@Composable
private fun LoginFormSection(
    username: String,
    password: String,
    rememberMe: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IndustrialCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Workstation Login",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter your credentials to access the system",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Error message display
                    if (errorMessage != null) {
                        IndustrialStatusText(
                            text = errorMessage,
                            isError = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Username field
                    IndustrialTextField(
                        value = username,
                        onValueChange = onUsernameChange,
                        label = "Username",
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password field
                    IndustrialTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = "Password",
                        isPassword = true,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Remember me checkbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = onRememberMeChange,
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Remember me on this device",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Login button
                    IndustrialButton(
                        text = "Sign In",
                        onClick = onLoginClick,
                        isLoading = isLoading,
                        enabled = username.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Forgot password button
                    IndustrialOutlinedButton(
                        text = "Forgot Password?",
                        onClick = onForgotPasswordClick,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Helper composable for system information rows.
 */
@Composable
private fun SystemInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(
    name = "Login Screen - Landscape",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun LoginScreenPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onLoginClick = { _, _ -> },
            onForgotPasswordClick = { },
            isLoading = false,
            errorMessage = null
        )
    }
}

@Preview(
    name = "Login Screen - With Error",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun LoginScreenErrorPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onLoginClick = { _, _ -> },
            onForgotPasswordClick = { },
            isLoading = false,
            errorMessage = "Invalid username or password"
        )
    }
}

@Preview(
    name = "Login Screen - Loading",
    widthDp = 1280,
    heightDp = 800,
    showBackground = true
)
@Composable
fun LoginScreenLoadingPreview() {
    HitPromoWorkstationTheme {
        LoginScreen(
            onLoginClick = { _, _ -> },
            onForgotPasswordClick = { },
            isLoading = true,
            errorMessage = null
        )
    }
}