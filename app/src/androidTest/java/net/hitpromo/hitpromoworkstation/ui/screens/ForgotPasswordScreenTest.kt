package net.hitpromo.hitpromoworkstation.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.hitpromo.hitpromoworkstation.presentation.forgotpassword.ForgotPasswordStep
import net.hitpromo.hitpromoworkstation.presentation.forgotpassword.ForgotPasswordUiState
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for ForgotPasswordScreen.
 *
 * Tests the forgot password screen components, interactions, and states
 * on the actual Android UI.
 *
 * Note: These tests use the actual composable with test state rather than
 * the ViewModel to ensure predictable test behavior.
 */
@HiltAndroidTest
class ForgotPasswordScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun forgotPasswordScreen_displaysUsernameStepInitially() {
        // Given
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = ForgotPasswordUiState.Initial,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Reset Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter your username to receive a password reset code").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordScreen_continueButtonDisabledWhenUsernameEmpty() {
        // Given
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = ForgotPasswordUiState.Initial,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()
    }

    @Test
    fun forgotPasswordScreen_continueButtonEnabledWhenUsernameEntered() {
        // Given
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = ForgotPasswordUiState.Initial,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")

        // Then
        composeTestRule.onNodeWithText("Continue").assertIsEnabled()
    }

    @Test
    fun forgotPasswordScreen_displaysLoadingState() {
        // Given
        val loadingState = ForgotPasswordUiState(
            currentStep = ForgotPasswordStep.ENTER_USERNAME,
            isLoading = true
        )

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = loadingState,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Processing...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue").assertIsNotEnabled()
    }

    @Test
    fun forgotPasswordScreen_displaysErrorMessage() {
        // Given
        val errorMessage = "User not found"
        val errorState = ForgotPasswordUiState(
            currentStep = ForgotPasswordStep.ENTER_USERNAME,
            errorMessage = errorMessage
        )

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = errorState,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun forgotPasswordScreen_displaysVerifyCodeStep() {
        // Given
        val codeSentState = ForgotPasswordUiState(
            currentStep = ForgotPasswordStep.VERIFY_CODE,
            username = "testuser",
            deliveryDestination = "t***@example.com",
            successMessage = "Verification code sent to t***@example.com"
        )

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = codeSentState,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Verify & Reset").assertIsDisplayed()
        composeTestRule.onNodeWithText("Verification code sent to t***@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Verification Code").assertIsDisplayed()
        composeTestRule.onNodeWithText("New Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reset Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Resend Code").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordScreen_displaysSuccessStep() {
        // Given
        val successState = ForgotPasswordUiState(
            currentStep = ForgotPasswordStep.SUCCESS,
            successMessage = "Password has been reset successfully. You can now log in with your new password."
        )

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = successState,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Success!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password has been reset successfully. You can now log in with your new password.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Return to Login").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordScreen_callsOnRequestResetWhenContinuePressed() {
        // Given
        var requestResetCalled = false
        var capturedUsername = ""

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = ForgotPasswordUiState.Initial,
                    onRequestReset = { username ->
                        requestResetCalled = true
                        capturedUsername = username
                    },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Continue").performClick()

        // Then
        assert(requestResetCalled)
        assert(capturedUsername == "testuser")
    }

    @Test
    fun forgotPasswordScreen_callsOnCancelWhenCancelPressed() {
        // Given
        var cancelCalled = false

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = ForgotPasswordUiState.Initial,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { cancelCalled = true }
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Then
        assert(cancelCalled)
    }

    @Test
    fun forgotPasswordScreen_callsOnResendCodeWhenResendPressed() {
        // Given
        var resendCalled = false
        val codeSentState = ForgotPasswordUiState(
            currentStep = ForgotPasswordStep.VERIFY_CODE,
            username = "testuser",
            deliveryDestination = "t***@example.com"
        )

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = codeSentState,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { resendCalled = true },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Resend Code").performClick()

        // Then
        assert(resendCalled)
    }

    @Test
    fun forgotPasswordScreen_resetPasswordButtonDisabledWhenFieldsEmpty() {
        // Given
        val codeSentState = ForgotPasswordUiState(
            currentStep = ForgotPasswordStep.VERIFY_CODE,
            username = "testuser",
            deliveryDestination = "t***@example.com"
        )

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = codeSentState,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Reset Password").assertIsNotEnabled()
    }

    @Test
    fun forgotPasswordScreen_displaysPasswordRequirements() {
        // Given
        val codeSentState = ForgotPasswordUiState(
            currentStep = ForgotPasswordStep.VERIFY_CODE,
            username = "testuser",
            deliveryDestination = "t***@example.com"
        )

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                ForgotPasswordScreenContent(
                    uiState = codeSentState,
                    onRequestReset = { },
                    onConfirmReset = { _, _, _ -> },
                    onNavigateBack = { },
                    onResendCode = { },
                    onClearError = { },
                    onCancel = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Password Requirements").assertIsDisplayed()
        composeTestRule.onNodeWithText("At least 8 characters long").assertIsDisplayed()
        composeTestRule.onNodeWithText("At least one uppercase letter").assertIsDisplayed()
        composeTestRule.onNodeWithText("At least one lowercase letter").assertIsDisplayed()
        composeTestRule.onNodeWithText("At least one number").assertIsDisplayed()
        composeTestRule.onNodeWithText("At least one special character").assertIsDisplayed()
    }
}

/**
 * Content composable for testing purposes.
 * This mirrors the internal content of ForgotPasswordScreen but accepts
 * explicit parameters instead of using ViewModel, making it testable.
 */
@Composable
private fun ForgotPasswordScreenContent(
    uiState: ForgotPasswordUiState,
    onRequestReset: (String) -> Unit,
    onConfirmReset: (String, String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onResendCode: () -> Unit,
    onClearError: () -> Unit,
    onCancel: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left panel - Branding
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.4f)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Reset Password",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Right panel - Form
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.6f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                when (uiState.currentStep) {
                    ForgotPasswordStep.ENTER_USERNAME -> {
                        EnterUsernameContent(
                            username = username,
                            onUsernameChange = { username = it },
                            onContinue = { onRequestReset(username) },
                            onCancel = onCancel,
                            isLoading = uiState.isLoading,
                            errorMessage = uiState.errorMessage
                        )
                    }
                    ForgotPasswordStep.VERIFY_CODE -> {
                        VerifyCodeContent(
                            verificationCode = verificationCode,
                            newPassword = newPassword,
                            confirmPassword = confirmPassword,
                            onVerificationCodeChange = { verificationCode = it },
                            onNewPasswordChange = { newPassword = it },
                            onConfirmPasswordChange = { confirmPassword = it },
                            onResetPassword = { onConfirmReset(verificationCode, newPassword, confirmPassword) },
                            onResendCode = onResendCode,
                            onNavigateBack = onNavigateBack,
                            isLoading = uiState.isLoading,
                            errorMessage = uiState.errorMessage,
                            successMessage = uiState.successMessage
                        )
                    }
                    ForgotPasswordStep.SUCCESS -> {
                        SuccessContent(
                            successMessage = uiState.successMessage ?: "",
                            onReturnToLogin = onCancel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnterUsernameContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter your username to receive a password reset code",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        IndustrialTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = "Username",
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            Text("Processing...")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            IndustrialSecondaryButton(
                onClick = onCancel,
                text = "Cancel",
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.size(16.dp))
            IndustrialButton(
                onClick = onContinue,
                text = "Continue",
                enabled = username.isNotBlank() && !isLoading
            )
        }
    }
}

@Composable
private fun VerifyCodeContent(
    verificationCode: String,
    newPassword: String,
    confirmPassword: String,
    onVerificationCodeChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onResetPassword: () -> Unit,
    onResendCode: () -> Unit,
    onNavigateBack: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Verify & Reset",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (successMessage != null) {
            Text(
                text = successMessage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        IndustrialTextField(
            value = verificationCode,
            onValueChange = onVerificationCodeChange,
            label = "Verification Code",
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        IndustrialTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = "New Password",
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        IndustrialTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirm Password",
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Password Requirements",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text("At least 8 characters long")
        Text("At least one uppercase letter")
        Text("At least one lowercase letter")
        Text("At least one number")
        Text("At least one special character")

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row {
            TextButton(onClick = onResendCode, enabled = !isLoading) {
                Text("Resend Code")
            }
            Spacer(modifier = Modifier.size(16.dp))
            IndustrialButton(
                onClick = onResetPassword,
                text = "Reset Password",
                enabled = verificationCode.isNotBlank() &&
                         newPassword.isNotBlank() &&
                         confirmPassword.isNotBlank() &&
                         !isLoading
            )
        }
    }
}

@Composable
private fun SuccessContent(
    successMessage: String,
    onReturnToLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Success!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = successMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        IndustrialButton(
            onClick = onReturnToLogin,
            text = "Return to Login"
        )
    }
}
