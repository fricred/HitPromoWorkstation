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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.hitpromo.hitpromoworkstation.R
import net.hitpromo.hitpromoworkstation.presentation.passwordchange.PasswordChangeIntent
import net.hitpromo.hitpromoworkstation.presentation.passwordchange.PasswordChangeViewModel
import net.hitpromo.hitpromoworkstation.presentation.passwordchange.PasswordField
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialButton
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialCard
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialLoadingIndicator
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialSecondaryButton
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialTextField
import net.hitpromo.hitpromoworkstation.ui.components.LoadingSize
import net.hitpromo.hitpromoworkstation.ui.components.PasswordRequirementsChecklist
import net.hitpromo.hitpromoworkstation.ui.components.PasswordStrengthIndicator

/**
 * Force Password Change Screen for AWS Cognito NEW_PASSWORD_REQUIRED challenge.
 *
 * Designed for Samsung Galaxy Tab A9+ (11" landscape) in production environment:
 * - Two-panel landscape layout matching LoginScreen design
 * - Left panel: Branding and instructions
 * - Right panel: Password change form with validation
 * - Large touch targets for gloved operation
 * - High contrast colors for factory floor visibility
 * - Real-time password strength feedback
 * - Accessibility-optimized with proper semantics
 *
 * @param username The username requiring password change
 * @param sessionId The session ID from NEW_PASSWORD_REQUIRED challenge
 * @param onPasswordChangeSuccess Callback when password change succeeds
 * @param onCancel Callback when user cancels the flow
 * @param modifier Modifier for the screen
 * @param viewModel The ViewModel handling password change logic
 */
@Composable
fun ForcePasswordChangeScreen(
    username: String,
    sessionId: String,
    onPasswordChangeSuccess: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PasswordChangeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val showSuccessMessage = remember { mutableStateOf(false) }

    // Initialize the ViewModel with the provided username and sessionId
    LaunchedEffect(username, sessionId) {
        viewModel.initialize(username, sessionId)
    }

    // Handle successful password change with delay to show success message
    // Use successTimestamp as key to prevent re-triggering on recomposition
    LaunchedEffect(uiState.successTimestamp) {
        if (uiState.isPasswordChangeSuccessful && uiState.successTimestamp != null) {
            showSuccessMessage.value = true
            delay(2500) // Show success message for 2.5 seconds
            onPasswordChangeSuccess()
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .semantics {
                    contentDescription = "Force password change screen"
                }
        ) {
        // LEFT PANEL - Branding and Instructions (40% width)
        Box(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Company Logo Placeholder
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 8.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "HP",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Hit Promotional Products",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Industrial Workstation",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Instructions Card
                IndustrialCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Password Change Required",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Your account requires a new password. Please create a strong password that meets the requirements shown.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "User: $username",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // RIGHT PANEL - Password Change Form (60% width)
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create New Password",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // New Password Field
                IndustrialTextField(
                    value = uiState.newPassword,
                    onValueChange = { password ->
                        viewModel.handleIntent(PasswordChangeIntent.NewPasswordChanged(password))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isFormEnabled,
                    label = "New Password",
                    placeholder = "Enter new password",
                    visualTransformation = if (uiState.isNewPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.handleIntent(
                                    PasswordChangeIntent.TogglePasswordVisibility(PasswordField.NEW_PASSWORD)
                                )
                            },
                            modifier = Modifier.semantics {
                                contentDescription = if (uiState.isNewPasswordVisible) {
                                    "Hide new password"
                                } else {
                                    "Show new password"
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (uiState.isNewPasswordVisible) {
                                        R.drawable.ic_visibility_off
                                    } else {
                                        R.drawable.ic_visibility
                                    }
                                ),
                                contentDescription = null, // contentDescription is in IconButton
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Password Field
                IndustrialTextField(
                    value = uiState.confirmPassword,
                    onValueChange = { password ->
                        viewModel.handleIntent(PasswordChangeIntent.ConfirmPasswordChanged(password))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isFormEnabled,
                    label = "Confirm Password",
                    placeholder = "Re-enter new password",
                    visualTransformation = if (uiState.isConfirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.handleIntent(
                                    PasswordChangeIntent.TogglePasswordVisibility(PasswordField.CONFIRM_PASSWORD)
                                )
                            },
                            modifier = Modifier.semantics {
                                contentDescription = if (uiState.isConfirmPasswordVisible) {
                                    "Hide confirm password"
                                } else {
                                    "Show confirm password"
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (uiState.isConfirmPasswordVisible) {
                                        R.drawable.ic_visibility_off
                                    } else {
                                        R.drawable.ic_visibility
                                    }
                                ),
                                contentDescription = null, // contentDescription is in IconButton
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (uiState.isSubmitEnabled) {
                                viewModel.handleIntent(PasswordChangeIntent.SubmitPasswordChange)
                            }
                        }
                    ),
                    singleLine = true,
                    isError = !uiState.passwordsMatch && uiState.confirmPassword.isNotEmpty()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Password Strength Indicator
                if (uiState.newPassword.isNotEmpty()) {
                    PasswordStrengthIndicator(
                        passwordStrength = uiState.passwordStrength,
                        modifier = Modifier.fillMaxWidth(),
                        showLabel = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Password Requirements Checklist
                PasswordRequirementsChecklist(
                    password = uiState.newPassword,
                    confirmPassword = uiState.confirmPassword,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Error Message
                if (uiState.hasError) {
                    Text(
                        text = uiState.errorMessage ?: "An error occurred",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                // Loading Indicator
                if (uiState.isLoading) {
                    IndustrialLoadingIndicator(
                        message = "Changing password...",
                        size = LoadingSize.MEDIUM,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IndustrialSecondaryButton(
                        onClick = {
                            viewModel.handleIntent(PasswordChangeIntent.Cancel)
                            onCancel()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = uiState.isFormEnabled
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    IndustrialButton(
                        onClick = {
                            viewModel.handleIntent(PasswordChangeIntent.SubmitPasswordChange)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = uiState.isSubmitEnabled
                    ) {
                        Text(
                            text = "Change Password",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
        }

        // Success Message Overlay
        if (showSuccessMessage.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .semantics {
                        contentDescription = "Password change successful"
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(48.dp)
                ) {
                    // Success Icon
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Password Changed Successfully!",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Redirecting to login...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}