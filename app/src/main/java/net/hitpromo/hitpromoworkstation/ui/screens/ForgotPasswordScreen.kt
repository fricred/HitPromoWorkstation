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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import net.hitpromo.hitpromoworkstation.R
import net.hitpromo.hitpromoworkstation.domain.usecase.PasswordStrength
import net.hitpromo.hitpromoworkstation.presentation.forgotpassword.ForgotPasswordIntent
import net.hitpromo.hitpromoworkstation.presentation.forgotpassword.ForgotPasswordStep
import net.hitpromo.hitpromoworkstation.presentation.forgotpassword.ForgotPasswordViewModel
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialButton
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialCard
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialLoadingIndicator
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialSecondaryButton
import net.hitpromo.hitpromoworkstation.ui.components.IndustrialTextField
import net.hitpromo.hitpromoworkstation.ui.components.LoadingSize
import net.hitpromo.hitpromoworkstation.ui.components.PasswordRequirementsChecklist
import net.hitpromo.hitpromoworkstation.ui.components.PasswordStrengthIndicator

/**
 * Forgot Password Screen for AWS Cognito password reset flow.
 *
 * Implements a multi-step password reset flow:
 * - Step 1: Enter username to request verification code
 * - Step 2: Enter verification code and new password
 * - Step 3: Success confirmation
 *
 * Designed for Samsung Galaxy Tab A9+ (11" landscape) in production environment:
 * - Two-panel landscape layout (40/60 split) matching LoginScreen design
 * - Left panel: Branding and instructions
 * - Right panel: Form content with step-based navigation
 * - Large touch targets for gloved operation
 * - High contrast colors for factory floor visibility
 * - Real-time password strength feedback
 * - Accessibility-optimized with proper semantics
 *
 * @param onComplete Callback when password reset completes successfully
 * @param onCancel Callback when user cancels the flow
 * @param modifier Modifier for the screen
 * @param viewModel The ViewModel handling forgot password logic
 */
@Composable
fun ForgotPasswordScreen(
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-navigate after successful password reset
    LaunchedEffect(uiState.currentStep) {
        if (uiState.currentStep == ForgotPasswordStep.SUCCESS) {
            delay(3000) // Show success message for 3 seconds
            viewModel.handleIntent(ForgotPasswordIntent.ReturnToLogin)
            onComplete()
        }
    }

    // Reset state when screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.handleIntent(ForgotPasswordIntent.ReturnToLogin)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .semantics {
                contentDescription = "Forgot password screen"
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // LEFT PANEL - Branding (40% width)
            BrandingPanel(
                currentStep = uiState.currentStep,
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            )

            // RIGHT PANEL - Content (60% width)
            ContentPanel(
                currentStep = uiState.currentStep,
                uiState = uiState,
                onIntent = viewModel::handleIntent,
                onCancel = onCancel,
                onComplete = onComplete,
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            )
        }
    }
}

/**
 * Left panel with branding and contextual instructions.
 *
 * @param currentStep The current step in the flow
 * @param modifier Modifier for the panel
 */
@Composable
private fun BrandingPanel(
    currentStep: ForgotPasswordStep,
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

            // Contextual instructions based on current step
            IndustrialCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    val (title, description) = when (currentStep) {
                        ForgotPasswordStep.ENTER_USERNAME -> Pair(
                            "Reset Your Password",
                            "Enter your username to receive a verification code. The code will be sent to your registered email address."
                        )
                        ForgotPasswordStep.VERIFY_CODE -> Pair(
                            "Verify and Reset",
                            "Enter the verification code sent to your email and create a new secure password."
                        )
                        ForgotPasswordStep.SUCCESS -> Pair(
                            "Success!",
                            "Your password has been reset successfully. You can now log in with your new password."
                        )
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Right panel containing the step-based form content.
 *
 * @param currentStep The current step in the flow
 * @param uiState The current UI state
 * @param onIntent Callback to handle user intents
 * @param onCancel Callback when user cancels
 * @param onComplete Callback when flow completes successfully
 * @param modifier Modifier for the panel
 */
@Composable
private fun ContentPanel(
    currentStep: ForgotPasswordStep,
    uiState: net.hitpromo.hitpromoworkstation.presentation.forgotpassword.ForgotPasswordUiState,
    onIntent: (ForgotPasswordIntent) -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            when (currentStep) {
                ForgotPasswordStep.ENTER_USERNAME -> EnterUsernameStep(
                    uiState = uiState,
                    onIntent = onIntent,
                    onCancel = onCancel
                )
                ForgotPasswordStep.VERIFY_CODE -> VerifyCodeAndPasswordStep(
                    uiState = uiState,
                    onIntent = onIntent
                )
                ForgotPasswordStep.SUCCESS -> SuccessStep(
                    onComplete = {
                        onIntent(ForgotPasswordIntent.ReturnToLogin)
                        onComplete()
                    }
                )
            }
        }
    }
}

/**
 * Step 1: Enter username to request verification code.
 *
 * @param uiState The current UI state
 * @param onIntent Callback to handle user intents
 * @param onCancel Callback when user cancels
 */
@Composable
private fun EnterUsernameStep(
    uiState: net.hitpromo.hitpromoworkstation.presentation.forgotpassword.ForgotPasswordUiState,
    onIntent: (ForgotPasswordIntent) -> Unit,
    onCancel: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Forgot Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter your username to receive a password reset code",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message display
        if (uiState.hasError) {
            IndustrialErrorText(
                text = uiState.errorMessage ?: "An error occurred",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // Username field
        IndustrialTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            placeholder = "Enter your username",
            enabled = uiState.isFormEnabled,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (username.isNotBlank() && uiState.isFormEnabled) {
                        onIntent(ForgotPasswordIntent.RequestPasswordReset(username))
                    }
                }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Loading indicator
        if (uiState.isLoading) {
            IndustrialLoadingIndicator(
                message = "Requesting verification code...",
                size = LoadingSize.MEDIUM,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IndustrialSecondaryButton(
                onClick = onCancel,
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
                    onIntent(ForgotPasswordIntent.RequestPasswordReset(username))
                },
                modifier = Modifier.weight(1f),
                enabled = username.isNotBlank() && uiState.isFormEnabled
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * Step 2: Enter verification code and new password.
 *
 * @param uiState The current UI state
 * @param onIntent Callback to handle user intents
 */
@Composable
private fun VerifyCodeAndPasswordStep(
    uiState: net.hitpromo.hitpromoworkstation.presentation.forgotpassword.ForgotPasswordUiState,
    onIntent: (ForgotPasswordIntent) -> Unit
) {
    var verificationCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Calculate password strength
    val passwordStrength = remember(newPassword) {
        calculatePasswordStrength(newPassword)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Code sent message
        if (uiState.deliveryDestination != null) {
            Text(
                text = "Verification code sent to ${uiState.deliveryDestination}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Error message display
        if (uiState.hasError) {
            IndustrialErrorText(
                text = uiState.errorMessage ?: "An error occurred",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // Verification code field
        IndustrialTextField(
            value = verificationCode,
            onValueChange = { verificationCode = it },
            label = "Verification Code",
            placeholder = "Enter 6-digit code",
            enabled = uiState.isFormEnabled,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // New password field
        IndustrialTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = "New Password",
            placeholder = "Enter new password",
            enabled = uiState.isFormEnabled,
            visualTransformation = if (isNewPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = { isNewPasswordVisible = !isNewPasswordVisible },
                    modifier = Modifier.semantics {
                        contentDescription = if (isNewPasswordVisible) {
                            "Hide new password"
                        } else {
                            "Show new password"
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isNewPasswordVisible) {
                                R.drawable.ic_visibility_off
                            } else {
                                R.drawable.ic_visibility
                            }
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
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

        // Confirm password field
        IndustrialTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            placeholder = "Re-enter new password",
            enabled = uiState.isFormEnabled,
            visualTransformation = if (isConfirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                    modifier = Modifier.semantics {
                        contentDescription = if (isConfirmPasswordVisible) {
                            "Hide confirm password"
                        } else {
                            "Show confirm password"
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isConfirmPasswordVisible) {
                                R.drawable.ic_visibility_off
                            } else {
                                R.drawable.ic_visibility
                            }
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (verificationCode.isNotBlank() && newPassword.isNotBlank() &&
                        confirmPassword.isNotBlank() && uiState.isFormEnabled
                    ) {
                        onIntent(
                            ForgotPasswordIntent.ConfirmPasswordReset(
                                verificationCode = verificationCode,
                                newPassword = newPassword,
                                confirmPassword = confirmPassword
                            )
                        )
                    }
                }
            ),
            singleLine = true,
            isError = !newPassword.isEmpty() && !confirmPassword.isEmpty() && newPassword != confirmPassword
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Password strength indicator
        if (newPassword.isNotEmpty()) {
            PasswordStrengthIndicator(
                passwordStrength = passwordStrength,
                modifier = Modifier.fillMaxWidth(),
                showLabel = true
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Password requirements checklist
        PasswordRequirementsChecklist(
            password = newPassword,
            confirmPassword = confirmPassword,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Resend code button
        TextButton(
            onClick = { onIntent(ForgotPasswordIntent.ResendCode) },
            enabled = uiState.isFormEnabled
        ) {
            Text(
                text = "Didn't receive code? Resend",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading indicator
        if (uiState.isLoading) {
            IndustrialLoadingIndicator(
                message = "Resetting password...",
                size = LoadingSize.MEDIUM,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IndustrialSecondaryButton(
                onClick = { onIntent(ForgotPasswordIntent.NavigateBack) },
                modifier = Modifier.weight(1f),
                enabled = uiState.isFormEnabled
            ) {
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            IndustrialButton(
                onClick = {
                    onIntent(
                        ForgotPasswordIntent.ConfirmPasswordReset(
                            verificationCode = verificationCode,
                            newPassword = newPassword,
                            confirmPassword = confirmPassword
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = verificationCode.isNotBlank() && newPassword.isNotBlank() &&
                        confirmPassword.isNotBlank() && uiState.isFormEnabled
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * Step 3: Success confirmation screen.
 *
 * @param onComplete Callback when user acknowledges success
 */
@Composable
private fun SuccessStep(
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
            text = "Password Reset Complete",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your password has been successfully reset. You can now log in with your new password.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Redirecting to login in 3 seconds...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Return to login button
        IndustrialButton(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(
                text = "Return to Login",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * Industrial-themed error text component.
 *
 * Displays error messages with high contrast and accessibility.
 *
 * @param text The error message text
 * @param modifier Modifier for the component
 */
@Composable
private fun IndustrialErrorText(
    text: String,
    modifier: Modifier = Modifier
) {
    IndustrialCard(
        modifier = modifier,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics {
                    contentDescription = "Error: $text"
                }
        )
    }
}

/**
 * Calculate password strength based on complexity.
 *
 * @param password The password to evaluate
 * @return PasswordStrength enum value
 */
private fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) {
        return PasswordStrength.WEAK
    }

    var score = 0

    // Length scoring
    score += when {
        password.length >= 12 -> 2
        password.length >= 8 -> 1
        else -> 0
    }

    // Character variety scoring
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    // Additional complexity bonus
    if (password.length >= 10 && score >= 4) score++

    return when {
        score <= 2 -> PasswordStrength.WEAK
        score == 3 -> PasswordStrength.MEDIUM
        score == 4 -> PasswordStrength.STRONG
        else -> PasswordStrength.VERY_STRONG
    }
}
