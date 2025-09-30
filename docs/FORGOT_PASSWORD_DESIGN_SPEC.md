# Forgot Password Flow - Design Specification

## Document Information
- **Project**: Hit Promotional Products Industrial Workstation
- **Feature**: Forgot Password / Password Reset Flow
- **Platform**: Android (Samsung Galaxy Tab A9+, 11" Landscape)
- **Target Users**: Production floor workers
- **Design System**: Industrial Theme with Material3
- **Authentication**: AWS Cognito

---

## 1. Overview

This document provides comprehensive UI/UX specifications for implementing the forgot password flow in the Hit Promotional Products Industrial Workstation tablet application. The design follows the established industrial design system with emphasis on large touch targets, high contrast, and accessibility for gloved operation.

### Design Principles
- **Industrial-First**: Large touch targets (64dp preferred) for gloved workers
- **High Contrast**: Enhanced visibility for bright production floor lighting
- **Error Prevention**: Clear validation and helpful feedback
- **Consistency**: Matches existing LoginScreen and ForcePasswordChangeScreen patterns
- **Accessibility**: WCAG 2.1 AA compliant with industrial enhancements

---

## 2. User Flow Diagram

```
┌─────────────────────┐
│   Login Screen      │
│                     │
│  [Forgot Password?] │◄─── Entry Point
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────────────────┐
│  Step 1: Request Reset              │
│  ─────────────────────────          │
│  Enter username or email            │
│  - Username/Email input field       │
│  - [Cancel] [Request Code] buttons  │
└──────────┬──────────────────────────┘
           │ (Success)
           ▼
┌─────────────────────────────────────┐
│  Step 2: Verify Code                │
│  ─────────────────────────          │
│  Enter verification code from email │
│  - 6-digit code input               │
│  - Resend code option               │
│  - [Back] [Verify Code] buttons     │
└──────────┬──────────────────────────┘
           │ (Success)
           ▼
┌─────────────────────────────────────┐
│  Step 3: Create New Password        │
│  ─────────────────────────          │
│  Set new password with validation   │
│  - New password field               │
│  - Confirm password field           │
│  - Password strength indicator      │
│  - Requirements checklist           │
│  - [Back] [Reset Password] buttons  │
└──────────┬──────────────────────────┘
           │ (Success)
           ▼
┌─────────────────────────────────────┐
│  Step 4: Success Confirmation       │
│  ─────────────────────────          │
│  Password reset successful          │
│  - Success message                  │
│  - Auto-redirect to login (3s)      │
│  - [Return to Login] button         │
└─────────────────────────────────────┘
```

### Error Paths
- **Any Step**: Network error → Show error message, allow retry
- **Any Step**: User cancels → Confirm dialog → Return to login
- **Step 1**: User not found → Show generic "check email" message (security)
- **Step 2**: Invalid/expired code → Show error, allow resend
- **Step 3**: Weak password → Show validation errors, prevent submission

---

## 3. Screen Specifications

### 3.1 Screen 1: Request Password Reset

**Purpose**: Collect username/email to initiate password reset flow

**Layout**: Two-panel landscape layout (40/60 split)

#### Left Panel (40% width) - Branding & Context
```
┌─────────────────────────────────────┐
│                                     │
│           [HP Logo]                 │
│      Hit Promotional Products       │
│      Industrial Workstation         │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ Password Reset                │ │
│  │                               │ │
│  │ We'll send a verification     │ │
│  │ code to your registered       │ │
│  │ email address.                │ │
│  │                               │ │
│  │ Check your email and enter    │ │
│  │ the code in the next step.    │ │
│  └───────────────────────────────┘ │
│                                     │
└─────────────────────────────────────┘
```

**Left Panel Components**:
- Company logo (120dp circle)
- Title: "Hit Promotional Products" (headlineLarge, bold)
- Subtitle: "Industrial Workstation" (titleLarge)
- IndustrialCard with instructions:
  - Title: "Password Reset" (titleLarge, primary color, bold)
  - Body: Clear explanation of the process (bodyLarge)
  - Card padding: 24dp
  - Card elevation: 8dp

#### Right Panel (60% width) - Reset Form
```
┌─────────────────────────────────────────────┐
│          Forgot Your Password?              │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ Username or Email                     │ │
│  │ [Enter username or email address]     │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  [?] Enter your username or the email      │
│      address associated with your account  │
│                                             │
│  ┌─────────────────┐  ┌─────────────────┐ │
│  │     Cancel      │  │  Request Code   │ │
│  └─────────────────┘  └─────────────────┘ │
│                                             │
└─────────────────────────────────────────────┘
```

**Right Panel Components**:

1. **Title**: "Forgot Your Password?"
   - Style: headlineMedium, bold
   - Color: onBackground
   - Padding bottom: 32dp

2. **Username/Email Input Field** (IndustrialTextField)
   - Label: "Username or Email"
   - Placeholder: "Enter username or email address"
   - Height: 64dp minimum
   - Width: fillMaxWidth
   - Keyboard type: Email
   - IME action: Done
   - Enabled: !isLoading
   - Error state: Shows if submission fails

3. **Help Text**
   - Text: "Enter your username or the email address associated with your account"
   - Style: bodyMedium
   - Color: onSurfaceVariant
   - Icon: Info icon (20dp)
   - Padding: 16dp vertical

4. **Action Buttons** (Row, equal weight)
   - **Cancel Button** (IndustrialSecondaryButton)
     - Text: "Cancel"
     - Width: weight(1f)
     - Height: 64dp
     - onClick: Show confirmation dialog
   - Spacer: 16dp
   - **Request Code Button** (IndustrialButton)
     - Text: "Request Code"
     - Width: weight(1f)
     - Height: 64dp
     - Enabled: usernameOrEmail.isNotBlank() && !isLoading
     - onClick: Submit reset request

**Loading State**:
- Show IndustrialLoadingIndicator (MEDIUM size)
- Message: "Sending verification code..."
- Disable all inputs and buttons

**Error State**:
- Show error message above buttons
- Style: bodyLarge, error color
- Keep form enabled for retry

**Success Transition**:
- Show success message briefly (500ms)
- Auto-navigate to Step 2

---

### 3.2 Screen 2: Verify Code

**Purpose**: Collect and verify the code sent to user's email

**Layout**: Two-panel landscape layout (40/60 split)

#### Left Panel (40% width) - Branding & Context
```
┌─────────────────────────────────────┐
│                                     │
│           [HP Logo]                 │
│      Hit Promotional Products       │
│      Industrial Workstation         │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ Verification Code Sent        │ │
│  │                               │ │
│  │ We've sent a 6-digit          │ │
│  │ verification code to:         │ │
│  │                               │ │
│  │ u***@example.com              │ │
│  │                               │ │
│  │ The code expires in 15        │ │
│  │ minutes.                      │ │
│  └───────────────────────────────┘ │
│                                     │
└─────────────────────────────────────┘
```

**Left Panel Components**:
- Same logo and branding as Step 1
- IndustrialCard with:
  - Title: "Verification Code Sent" (titleLarge, primary color, bold)
  - Masked email display (bodyLarge, SemiBold)
  - Expiration notice (bodyMedium, onSurfaceVariant)

#### Right Panel (60% width) - Code Entry Form
```
┌─────────────────────────────────────────────┐
│          Enter Verification Code            │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ Verification Code                     │ │
│  │ [Enter 6-digit code]                  │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  [?] Check your email for the code         │
│                                             │
│  Didn't receive the code?                  │
│  [Resend Code] (Available in 60s)          │
│                                             │
│  ┌─────────────────┐  ┌─────────────────┐ │
│  │      Back       │  │  Verify Code    │ │
│  └─────────────────┘  └─────────────────┘ │
│                                             │
└─────────────────────────────────────────────┘
```

**Right Panel Components**:

1. **Title**: "Enter Verification Code"
   - Style: headlineMedium, bold
   - Color: onBackground
   - Padding bottom: 32dp

2. **Verification Code Input** (IndustrialTextField)
   - Label: "Verification Code"
   - Placeholder: "Enter 6-digit code"
   - Height: 64dp minimum
   - Width: fillMaxWidth
   - Keyboard type: Number
   - Max length: 6 characters
   - IME action: Done
   - Text style: Large monospace for readability
   - Visual feedback: Auto-submit when 6 digits entered
   - Error state: Shows if code invalid

3. **Help Text**
   - Text: "Check your email for the code"
   - Style: bodyMedium
   - Color: onSurfaceVariant
   - Icon: Info icon (20dp)
   - Padding: 16dp vertical

4. **Resend Code Section**
   - Label: "Didn't receive the code?" (bodyMedium)
   - **Resend Button** (TextButton style)
     - Text: "Resend Code"
     - Color: primary
     - Enabled: After countdown expires (60 seconds)
     - Shows countdown: "Resend Code (45s)" while disabled
   - Padding: 24dp vertical

5. **Action Buttons** (Row, equal weight)
   - **Back Button** (IndustrialSecondaryButton)
     - Text: "Back"
     - Width: weight(1f)
     - Height: 64dp
     - onClick: Return to Step 1
   - Spacer: 16dp
   - **Verify Code Button** (IndustrialButton)
     - Text: "Verify Code"
     - Width: weight(1f)
     - Height: 64dp
     - Enabled: code.length == 6 && !isLoading
     - onClick: Verify code with Cognito

**Loading State**:
- Show IndustrialLoadingIndicator (MEDIUM size)
- Message: "Verifying code..."
- Disable all inputs and buttons

**Error States**:
- **Invalid Code**: "Invalid verification code. Please try again."
- **Expired Code**: "This code has expired. Please request a new one."
- **Too Many Attempts**: "Too many failed attempts. Please request a new code."
- Display error above buttons, keep form enabled

**Success Transition**:
- Show success message briefly (500ms)
- Auto-navigate to Step 3

**Countdown Timer**:
- Start at 60 seconds
- Display format: "(Xs remaining)"
- When expired: Enable resend button

---

### 3.3 Screen 3: Create New Password

**Purpose**: Allow user to create a new password with validation

**Layout**: Two-panel landscape layout (40/60 split)

#### Left Panel (40% width) - Branding & Instructions
```
┌─────────────────────────────────────┐
│                                     │
│           [HP Logo]                 │
│      Hit Promotional Products       │
│      Industrial Workstation         │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ Create New Password           │ │
│  │                               │ │
│  │ Choose a strong password      │ │
│  │ that meets all the            │ │
│  │ requirements shown.           │ │
│  │                               │ │
│  │ Your password must be         │ │
│  │ different from your           │ │
│  │ previous password.            │ │
│  └───────────────────────────────┘ │
│                                     │
└─────────────────────────────────────┘
```

**Left Panel Components**:
- Same logo and branding as previous steps
- IndustrialCard with:
  - Title: "Create New Password" (titleLarge, primary color, bold)
  - Instructions (bodyLarge)
  - Important note about previous password (bodyMedium, onSurfaceVariant)

#### Right Panel (60% width) - Password Creation Form
```
┌─────────────────────────────────────────────┐
│          Set Your New Password              │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ New Password                          │ │
│  │ [Enter new password]             👁   │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  ┌───────────────────────────────────────┐ │
│  │ Confirm Password                      │ │
│  │ [Re-enter new password]          👁   │ │
│  └───────────────────────────────────────┘ │
│                                             │
│  ┌─────────────────────────────────────┐  │
│  │ Password Strength: [====    ] Medium│  │
│  └─────────────────────────────────────┘  │
│                                             │
│  Password Requirements:                     │
│  ✓ At least 8 characters                   │
│  ✓ One uppercase letter (A-Z)              │
│  ○ One lowercase letter (a-z)              │
│  ○ One number (0-9)                        │
│  ○ One special character (!@#$%^&*)        │
│  ○ Passwords match                         │
│                                             │
│  ┌─────────────────┐  ┌─────────────────┐ │
│  │      Back       │  │ Reset Password  │ │
│  └─────────────────┘  └─────────────────┘ │
│                                             │
└─────────────────────────────────────────────┘
```

**Right Panel Components**:

1. **Title**: "Set Your New Password"
   - Style: headlineMedium, bold
   - Color: onBackground
   - Padding bottom: 32dp

2. **New Password Field** (IndustrialTextField)
   - Label: "New Password"
   - Placeholder: "Enter new password"
   - Height: 64dp minimum
   - Width: fillMaxWidth
   - Keyboard type: Password
   - IME action: Next
   - Visual transformation: PasswordVisualTransformation (toggleable)
   - Trailing icon: Visibility toggle (24dp IconButton)
   - Error state: Shows validation errors

3. **Confirm Password Field** (IndustrialTextField)
   - Label: "Confirm Password"
   - Placeholder: "Re-enter new password"
   - Height: 64dp minimum
   - Width: fillMaxWidth
   - Keyboard type: Password
   - IME action: Done
   - Visual transformation: PasswordVisualTransformation (toggleable)
   - Trailing icon: Visibility toggle (24dp IconButton)
   - Error state: Shows if passwords don't match
   - Spacer below: 24dp

4. **Password Strength Indicator** (PasswordStrengthIndicator component)
   - Display only when newPassword.isNotEmpty()
   - Shows: Weak / Medium / Strong / Very Strong
   - Color coded: Red / Amber / Green / Green
   - Animated progress bar
   - Spacer below: 24dp

5. **Password Requirements Checklist** (PasswordRequirementsChecklist component)
   - Title: "Password Requirements" (titleMedium, bold)
   - Real-time validation display:
     - ✓ (green) = requirement met
     - ○ (red) = requirement not met
   - Requirements:
     - At least 8 characters
     - One uppercase letter (A-Z)
     - One lowercase letter (a-z)
     - One number (0-9)
     - One special character (!@#$%^&*)
     - Passwords match (only shown when confirm field has content)
   - Each requirement: Row with icon + text
   - Text style: bodyLarge
   - Spacing: 8dp between requirements
   - Spacer below: 32dp

6. **Action Buttons** (Row, equal weight)
   - **Back Button** (IndustrialSecondaryButton)
     - Text: "Back"
     - Width: weight(1f)
     - Height: 64dp
     - onClick: Return to Step 2
   - Spacer: 16dp
   - **Reset Password Button** (IndustrialButton)
     - Text: "Reset Password"
     - Width: weight(1f)
     - Height: 64dp
     - Enabled: All requirements met && !isLoading
     - onClick: Submit password reset

**Loading State**:
- Show IndustrialLoadingIndicator (MEDIUM size)
- Message: "Resetting password..."
- Disable all inputs and buttons

**Error States**:
- **Password Too Weak**: Inline validation via checklist
- **Passwords Don't Match**: Error on confirm field
- **Network Error**: Show error message above buttons
- **Server Error**: "Failed to reset password. Please try again."

**Success Transition**:
- Show success overlay (see Step 4)
- Auto-navigate to login after 2.5 seconds

---

### 3.4 Screen 4: Success Confirmation

**Purpose**: Confirm successful password reset and redirect to login

**Layout**: Full-screen overlay on top of Step 3

```
┌─────────────────────────────────────────────┐
│                                             │
│                                             │
│              ┌───────────┐                  │
│              │     ✓     │                  │
│              └───────────┘                  │
│                                             │
│      Password Reset Successful!             │
│                                             │
│      You can now sign in with your          │
│      new password.                          │
│                                             │
│      Redirecting to login...                │
│                                             │
│      ┌─────────────────────┐               │
│      │  Return to Login    │               │
│      └─────────────────────┘               │
│                                             │
└─────────────────────────────────────────────┘
```

**Components**:

1. **Overlay Background**
   - Color: surface.copy(alpha = 0.95f)
   - Covers entire screen
   - Semantic description: "Password reset successful"

2. **Success Icon**
   - Style: Large circular surface (120dp)
   - Background: primaryContainer
   - Elevation: 8dp
   - Content: "✓" checkmark
   - Font size: 72sp
   - Color: primary
   - Bold weight

3. **Success Title**
   - Text: "Password Reset Successful!"
   - Style: headlineLarge, bold
   - Color: onSurface
   - Text align: Center
   - Padding top: 32dp

4. **Success Message**
   - Text: "You can now sign in with your new password."
   - Style: bodyLarge
   - Color: onSurfaceVariant
   - Text align: Center
   - Padding top: 16dp

5. **Redirect Message**
   - Text: "Redirecting to login..."
   - Style: bodyMedium
   - Color: onSurfaceVariant
   - Text align: Center
   - Padding top: 8dp
   - Padding bottom: 24dp

6. **Return Button** (IndustrialButton) (Optional - auto-redirects)
   - Text: "Return to Login"
   - Width: Fixed 300dp or fillMaxWidth(0.4f)
   - Height: 64dp
   - onClick: Immediate navigation to login

**Behavior**:
- Auto-dismiss after 2.5 seconds
- Navigate to LoginScreen
- Clear all reset flow state
- Optional: Pre-fill username on login screen

---

## 4. State Management

### 4.1 Screen States

Each screen should handle these states:

```kotlin
data class ForgotPasswordState(
    // Current step in the flow
    val currentStep: ForgotPasswordStep = ForgotPasswordStep.REQUEST_RESET,

    // Step 1: Request Reset
    val usernameOrEmail: String = "",

    // Step 2: Verify Code
    val verificationCode: String = "",
    val maskedEmail: String = "",
    val resendCountdown: Int = 60,
    val canResendCode: Boolean = false,

    // Step 3: New Password
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val passwordStrength: PasswordStrength = PasswordStrength.WEAK,
    val passwordsMatch: Boolean = false,
    val allRequirementsMet: Boolean = false,

    // Common states
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successTimestamp: Long? = null,

    // Session tracking
    val resetSessionId: String? = null
)

enum class ForgotPasswordStep {
    REQUEST_RESET,    // Step 1
    VERIFY_CODE,      // Step 2
    CREATE_PASSWORD,  // Step 3
    SUCCESS          // Step 4
}
```

### 4.2 Validation Rules

**Username/Email Validation**:
```kotlin
fun validateUsernameOrEmail(input: String): Boolean {
    return input.isNotBlank() &&
           (input.length >= 3 || input.contains("@"))
}
```

**Verification Code Validation**:
```kotlin
fun validateVerificationCode(code: String): Boolean {
    return code.length == 6 && code.all { it.isDigit() }
}
```

**Password Validation** (reuse existing PasswordRequirements):
```kotlin
data class PasswordRequirements(
    val hasMinLength: Boolean,      // >= 8 characters
    val hasUppercase: Boolean,      // A-Z
    val hasLowercase: Boolean,      // a-z
    val hasDigit: Boolean,          // 0-9
    val hasSpecialChar: Boolean     // !@#$%^&*
) {
    val allMet: Boolean = hasMinLength && hasUppercase &&
                         hasLowercase && hasDigit && hasSpecialChar
}
```

---

## 5. AWS Cognito Integration

### 5.1 Cognito Flow

The forgot password flow uses AWS Cognito's password reset functionality:

```kotlin
// Step 1: Initiate Reset
suspend fun initiatePasswordReset(usernameOrEmail: String): Result<String> {
    return try {
        val result = Amplify.Auth.resetPassword(usernameOrEmail)
        // Returns destination (masked email) where code was sent
        Result.success(result.destination)
    } catch (e: AuthException) {
        Result.failure(e)
    }
}

// Step 2: Verify Code + Set New Password (Combined in Cognito)
suspend fun confirmPasswordReset(
    usernameOrEmail: String,
    code: String,
    newPassword: String
): Result<Unit> {
    return try {
        Amplify.Auth.confirmResetPassword(
            username = usernameOrEmail,
            newPassword = newPassword,
            confirmationCode = code
        )
        Result.success(Unit)
    } catch (e: AuthException) {
        Result.failure(e)
    }
}

// Resend Code
suspend fun resendPasswordResetCode(usernameOrEmail: String): Result<String> {
    return try {
        val result = Amplify.Auth.resetPassword(usernameOrEmail)
        Result.success(result.destination)
    } catch (e: AuthException) {
        Result.failure(e)
    }
}
```

### 5.2 Error Handling

Map Cognito exceptions to user-friendly messages:

```kotlin
fun handleCognitoException(exception: AuthException): String {
    return when {
        exception.message?.contains("UserNotFoundException") == true ->
            "If an account exists, a code has been sent to your email."
        exception.message?.contains("LimitExceededException") == true ->
            "Too many attempts. Please try again later."
        exception.message?.contains("CodeMismatchException") == true ->
            "Invalid verification code. Please try again."
        exception.message?.contains("ExpiredCodeException") == true ->
            "This code has expired. Please request a new one."
        exception.message?.contains("InvalidPasswordException") == true ->
            "Password does not meet requirements."
        exception.message?.contains("InvalidParameterException") == true ->
            "Invalid input. Please check your information."
        else ->
            "An error occurred. Please try again."
    }
}
```

### 5.3 Security Considerations

1. **Generic Error Messages**: Don't reveal if user exists (security best practice)
2. **Rate Limiting**: Cognito handles this server-side
3. **Code Expiration**: Codes expire in 15 minutes (Cognito default)
4. **Session Management**: Track reset session locally to manage multi-step flow
5. **Input Sanitization**: Trim whitespace, lowercase email addresses

---

## 6. Component Reuse

### 6.1 Existing Components to Use

From `IndustrialComponents.kt`:
- **IndustrialButton**: Primary action buttons
- **IndustrialSecondaryButton**: Cancel/Back buttons
- **IndustrialTextField**: All text inputs
- **IndustrialCard**: Information cards in left panel
- **IndustrialLoadingIndicator**: Loading states

From `PasswordStrengthIndicator.kt`:
- **PasswordStrengthIndicator**: Show password strength in Step 3

From `PasswordRequirementsChecklist.kt`:
- **PasswordRequirementsChecklist**: Show requirements in Step 3

### 6.2 New Components Needed

None! All necessary components already exist in the design system.

---

## 7. Accessibility Specifications

### 7.1 Touch Targets

- All buttons: 64dp minimum height
- All text fields: 64dp minimum height
- Visibility toggle icons: 48dp tap area
- Resend code button: 48dp minimum height
- Spacing between touch targets: 16dp minimum

### 7.2 Semantic Descriptions

```kotlin
// Screen-level semantics
Modifier.semantics {
    contentDescription = when (currentStep) {
        REQUEST_RESET -> "Password reset request screen"
        VERIFY_CODE -> "Verification code entry screen"
        CREATE_PASSWORD -> "New password creation screen"
        SUCCESS -> "Password reset successful"
    }
}

// Component semantics
IndustrialTextField(
    value = code,
    onValueChange = { /* ... */ },
    modifier = Modifier.semantics {
        contentDescription = "Verification code input, enter 6 digits"
    }
)

// Button semantics
IndustrialButton(
    onClick = { /* ... */ },
    modifier = Modifier.semantics {
        contentDescription = "Reset password and return to login"
    }
)
```

### 7.3 Screen Reader Support

- All form fields have labels
- Error messages announced automatically
- Loading states announced
- Success confirmation announced
- Step progress announced ("Step 1 of 3")

### 7.4 Color Contrast

All text and UI elements meet WCAG 2.1 AA standards:
- Normal text: 4.5:1 minimum (7:1 target for industrial)
- Large text: 3:0:1 minimum (4.5:1 target for industrial)
- UI components: 3:1 minimum

### 7.5 Keyboard Navigation

- Tab order: Top to bottom, left to right
- IME actions: Next → Next → Done
- Enter key: Submits current step
- Escape key: Triggers cancel (with confirmation)

---

## 8. Error Handling & Edge Cases

### 8.1 Network Errors

**Scenario**: No internet connection or timeout

**Handling**:
- Check network status before API calls
- Show error message: "Network unavailable. Please check your connection."
- Keep form state intact
- Enable retry without losing entered data
- Timeout after 30 seconds

### 8.2 Invalid Input

**Scenario**: User enters invalid username, code, or password

**Handling**:
- Inline validation as user types
- Error messages below fields
- Prevent submission with disabled button
- Clear, specific error messages
- Visual error indicators (red border, error text)

### 8.3 Session Expiration

**Scenario**: User takes too long between steps

**Handling**:
- Verification code expires: Show "Code expired" message, offer resend
- Reset session expires: Restart flow from Step 1
- Display remaining time for urgent actions
- Auto-save form state locally (except passwords)

### 8.4 User Cancellation

**Scenario**: User clicks Cancel or Back

**Handling**:
- Show confirmation dialog:
  - Title: "Cancel Password Reset?"
  - Message: "Your progress will be lost."
  - Actions: "Continue Reset" (secondary) | "Cancel Reset" (primary)
- Clear all state on confirmation
- Return to LoginScreen
- Log cancellation event for analytics

### 8.5 Multiple Reset Attempts

**Scenario**: User requests multiple codes in short time

**Handling**:
- Cognito rate limiting prevents abuse
- Show error: "Too many attempts. Please wait before trying again."
- Display countdown timer if available
- Suggest contacting support after 3 failed attempts

### 8.6 User Not Found

**Scenario**: Username/email doesn't exist

**Handling**:
- Show generic success message (security best practice)
- "If an account exists with this email, we've sent a verification code."
- Don't reveal if user exists in system
- Code verification will fail naturally if no user

### 8.7 Password Reuse

**Scenario**: User tries to use same password as before

**Handling**:
- Cognito may reject (depends on configuration)
- Show error: "Please choose a different password."
- Keep other form data intact
- Suggest password variations

---

## 9. Navigation & Routing

### 9.1 Navigation Graph

```kotlin
// In your navigation setup
sealed class Screen {
    object Login : Screen()
    object ForgotPassword : Screen()
    // ... other screens
}

// Navigation routes
NavHost(navController, startDestination = Screen.Login) {
    composable<Screen.Login> {
        LoginScreen(
            onForgotPasswordClick = {
                navController.navigate(Screen.ForgotPassword)
            }
        )
    }

    composable<Screen.ForgotPassword> {
        ForgotPasswordScreen(
            onPasswordResetSuccess = {
                navController.navigate(Screen.Login) {
                    popUpTo(Screen.Login) { inclusive = true }
                }
            },
            onCancel = {
                navController.popBackStack()
            }
        )
    }
}
```

### 9.2 Back Button Behavior

- **Step 1**: Back button → Return to Login
- **Step 2**: Back button → Return to Step 1 (keeps username)
- **Step 3**: Back button → Return to Step 2 (loses password data)
- **Step 4**: Back button → Disabled (auto-redirects)
- **All steps**: System back → Show cancel confirmation dialog

### 9.3 Deep Linking

Support deep linking to forgot password flow:
```
hitpromo://forgot-password
```

---

## 10. Animation & Transitions

### 10.1 Screen Transitions

```kotlin
// Slide transition between steps
AnimatedContent(
    targetState = currentStep,
    transitionSpec = {
        if (targetState > initialState) {
            // Moving forward
            slideInHorizontally { it } + fadeIn() with
            slideOutHorizontally { -it } + fadeOut()
        } else {
            // Moving backward
            slideInHorizontally { -it } + fadeIn() with
            slideOutHorizontally { it } + fadeOut()
        }
    }
)
```

### 10.2 Component Animations

- **Loading indicator**: Rotate continuously
- **Password strength bar**: Animate width change (300ms)
- **Checklist items**: Fade in checkmark (150ms)
- **Error messages**: Slide in from top (200ms)
- **Success overlay**: Fade in (300ms), scale in icon (400ms)
- **Button state changes**: Crossfade colors (200ms)

### 10.3 Timing

- Fast: 150ms (micro-interactions)
- Standard: 300ms (most animations)
- Slow: 500ms (screen transitions)
- Delay: 2500ms (success auto-redirect)

---

## 11. Testing Checklist

### 11.1 Functional Testing

- [ ] Request reset with valid username
- [ ] Request reset with valid email
- [ ] Request reset with invalid input
- [ ] Verify valid 6-digit code
- [ ] Verify invalid code
- [ ] Verify expired code
- [ ] Resend verification code
- [ ] Countdown timer works correctly
- [ ] Create password meeting all requirements
- [ ] Create password failing requirements
- [ ] Password visibility toggle works
- [ ] Passwords match validation
- [ ] Navigate forward through all steps
- [ ] Navigate backward through steps
- [ ] Cancel from each step
- [ ] Network error handling
- [ ] Session timeout handling
- [ ] Success redirect to login

### 11.2 Accessibility Testing

- [ ] All touch targets minimum 64dp
- [ ] Screen reader announces all content
- [ ] Error messages announced
- [ ] Focus order logical
- [ ] Keyboard navigation works
- [ ] Color contrast meets standards
- [ ] Text size scalable to 200%
- [ ] TalkBack compatibility
- [ ] Switch control compatibility

### 11.3 Visual Testing

- [ ] Layout correct in landscape
- [ ] Text readable under bright light
- [ ] Colors match design system
- [ ] Spacing consistent throughout
- [ ] Loading states display correctly
- [ ] Error states display correctly
- [ ] Success animation plays
- [ ] Animations smooth (60fps)
- [ ] No text truncation
- [ ] Icons properly sized

### 11.4 Edge Case Testing

- [ ] Very long username/email
- [ ] Special characters in input
- [ ] Multiple rapid submissions
- [ ] Network disconnects mid-flow
- [ ] App backgrounded during flow
- [ ] System back button
- [ ] Screen rotation (should lock landscape)
- [ ] Low memory conditions
- [ ] Slow network conditions
- [ ] Gloved finger interaction

### 11.5 Device Testing

- [ ] Samsung Galaxy Tab A9+ (primary)
- [ ] Various Android tablet sizes
- [ ] Different Android versions
- [ ] Different screen densities
- [ ] Different production floor lighting

---

## 12. Implementation Notes

### 12.1 File Structure

Suggested file organization:

```
app/src/main/java/net/hitpromo/hitpromoworkstation/
├── ui/
│   └── screens/
│       └── ForgotPasswordScreen.kt
├── presentation/
│   └── forgotpassword/
│       ├── ForgotPasswordViewModel.kt
│       ├── ForgotPasswordState.kt
│       └── ForgotPasswordIntent.kt
├── domain/
│   └── usecase/
│       ├── ResetPasswordUseCase.kt
│       ├── VerifyResetCodeUseCase.kt
│       └── ConfirmPasswordResetUseCase.kt
└── data/
    └── repository/
        └── AuthRepository.kt (extend existing)
```

### 12.2 Composable Structure

```kotlin
@Composable
fun ForgotPasswordScreen(
    onPasswordResetSuccess: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState.currentStep) {
            ForgotPasswordStep.REQUEST_RESET -> {
                RequestResetStep(
                    state = uiState,
                    onUsernameChange = { viewModel.handleIntent(UpdateUsername(it)) },
                    onSubmit = { viewModel.handleIntent(RequestReset) },
                    onCancel = onCancel
                )
            }
            ForgotPasswordStep.VERIFY_CODE -> {
                VerifyCodeStep(
                    state = uiState,
                    onCodeChange = { viewModel.handleIntent(UpdateCode(it)) },
                    onSubmit = { viewModel.handleIntent(VerifyCode) },
                    onResend = { viewModel.handleIntent(ResendCode) },
                    onBack = { viewModel.handleIntent(NavigateBack) }
                )
            }
            ForgotPasswordStep.CREATE_PASSWORD -> {
                CreatePasswordStep(
                    state = uiState,
                    onNewPasswordChange = { viewModel.handleIntent(UpdateNewPassword(it)) },
                    onConfirmPasswordChange = { viewModel.handleIntent(UpdateConfirmPassword(it)) },
                    onTogglePasswordVisibility = { field ->
                        viewModel.handleIntent(TogglePasswordVisibility(field))
                    },
                    onSubmit = { viewModel.handleIntent(ConfirmPasswordReset) },
                    onBack = { viewModel.handleIntent(NavigateBack) }
                )
            }
            ForgotPasswordStep.SUCCESS -> {
                SuccessOverlay(
                    onReturnToLogin = onPasswordResetSuccess
                )
            }
        }
    }
}
```

### 12.3 ViewModel Pattern

```kotlin
class ForgotPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val verifyResetCodeUseCase: VerifyResetCodeUseCase,
    private val confirmPasswordResetUseCase: ConfirmPasswordResetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordState())
    val uiState: StateFlow<ForgotPasswordState> = _uiState.asStateFlow()

    fun handleIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is UpdateUsername -> updateUsername(intent.username)
            is RequestReset -> requestReset()
            is UpdateCode -> updateCode(intent.code)
            is VerifyCode -> verifyCode()
            is ResendCode -> resendCode()
            is UpdateNewPassword -> updateNewPassword(intent.password)
            is UpdateConfirmPassword -> updateConfirmPassword(intent.password)
            is TogglePasswordVisibility -> togglePasswordVisibility(intent.field)
            is ConfirmPasswordReset -> confirmReset()
            is NavigateBack -> navigateBack()
        }
    }

    // Implementation methods...
}
```

### 12.4 Repository Methods

Add to existing `AuthRepository`:

```kotlin
interface AuthRepository {
    // Existing methods...

    /**
     * Initiate password reset flow.
     * Sends verification code to user's email.
     */
    suspend fun initiatePasswordReset(usernameOrEmail: String): AuthResult<String>

    /**
     * Verify reset code and set new password.
     */
    suspend fun confirmPasswordReset(
        usernameOrEmail: String,
        code: String,
        newPassword: String
    ): AuthResult<Unit>

    /**
     * Resend password reset verification code.
     */
    suspend fun resendPasswordResetCode(usernameOrEmail: String): AuthResult<String>
}
```

### 12.5 Use Cases

```kotlin
class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(usernameOrEmail: String): Result<String> {
        return try {
            when (val result = authRepository.initiatePasswordReset(usernameOrEmail)) {
                is AuthResult.Success -> Result.success(result.data)
                is AuthResult.Error -> Result.failure(
                    Exception(result.message)
                )
                else -> Result.failure(Exception("Unexpected result"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## 13. Analytics & Logging

### 13.1 Events to Track

```kotlin
// Track flow progression
analytics.logEvent("forgot_password_started")
analytics.logEvent("forgot_password_code_requested")
analytics.logEvent("forgot_password_code_verified")
analytics.logEvent("forgot_password_completed")
analytics.logEvent("forgot_password_cancelled", mapOf("step" to currentStep.name))

// Track errors
analytics.logEvent("forgot_password_error", mapOf(
    "step" to currentStep.name,
    "error_type" to errorType
))

// Track interactions
analytics.logEvent("forgot_password_resend_code")
analytics.logEvent("forgot_password_password_visibility_toggled")
```

### 13.2 Performance Metrics

- Time spent on each step
- Time to complete full flow
- Drop-off rate per step
- Success rate
- Error frequency by type

### 13.3 Debug Logging

```kotlin
if (BuildConfig.DEBUG) {
    Log.d(TAG, "Forgot password step: ${currentStep.name}")
    Log.d(TAG, "Initiating reset for user: ${usernameOrEmail}")
    Log.d(TAG, "Code verification: ${if (isValid) "success" else "failed"}")
}
```

---

## 14. Localization Considerations

### 14.1 String Resources

All user-facing text should be in `strings.xml`:

```xml
<!-- Forgot Password Flow -->
<string name="forgot_password_title">Forgot Your Password?</string>
<string name="forgot_password_request_title">Request Password Reset</string>
<string name="forgot_password_verify_title">Enter Verification Code</string>
<string name="forgot_password_create_title">Set Your New Password</string>
<string name="forgot_password_success_title">Password Reset Successful!</string>

<!-- Instructions -->
<string name="forgot_password_request_instructions">We\'ll send a verification code to your registered email address.</string>
<string name="forgot_password_verify_instructions">Check your email for the 6-digit code.</string>
<string name="forgot_password_create_instructions">Choose a strong password that meets all the requirements.</string>

<!-- Form Labels -->
<string name="forgot_password_username_label">Username or Email</string>
<string name="forgot_password_code_label">Verification Code</string>
<string name="forgot_password_new_password_label">New Password</string>
<string name="forgot_password_confirm_password_label">Confirm Password</string>

<!-- Buttons -->
<string name="forgot_password_request_code">Request Code</string>
<string name="forgot_password_verify_code">Verify Code</string>
<string name="forgot_password_reset_password">Reset Password</string>
<string name="forgot_password_resend_code">Resend Code</string>
<string name="forgot_password_return_to_login">Return to Login</string>

<!-- Error Messages -->
<string name="forgot_password_error_invalid_username">Please enter a valid username or email</string>
<string name="forgot_password_error_invalid_code">Invalid verification code</string>
<string name="forgot_password_error_expired_code">This code has expired</string>
<string name="forgot_password_error_password_weak">Password does not meet requirements</string>
<string name="forgot_password_error_passwords_mismatch">Passwords do not match</string>
<string name="forgot_password_error_network">Network error. Please try again.</string>
<string name="forgot_password_error_too_many_attempts">Too many attempts. Please try again later.</string>

<!-- Success Messages -->
<string name="forgot_password_code_sent">Code sent to your email</string>
<string name="forgot_password_success_message">You can now sign in with your new password</string>
<string name="forgot_password_redirecting">Redirecting to login…</string>

<!-- Content Descriptions (Accessibility) -->
<string name="forgot_password_cd_password_visibility">Toggle password visibility</string>
<string name="forgot_password_cd_success_icon">Password reset successful checkmark</string>
```

### 14.2 RTL Support

- Layout should mirror for RTL languages
- Use `start`/`end` instead of `left`/`right`
- Test with Arabic or Hebrew locale
- Icons that indicate direction should flip

---

## 15. Future Enhancements

### 15.1 Phase 2 Features

- **Biometric reset**: Use fingerprint to reset password
- **Security questions**: Additional verification method
- **SMS verification**: Alternative to email code
- **Password manager integration**: Suggest/save strong passwords
- **Account recovery**: Multi-factor recovery options

### 15.2 UX Improvements

- **Progress indicator**: Show "Step 1 of 3" at top
- **Auto-fill code**: Detect SMS and auto-fill code
- **Password suggestions**: Generate strong password suggestions
- **Estimated time**: Show "Takes about 2 minutes"
- **Help button**: Context-sensitive help on each screen

### 15.3 Technical Improvements

- **Offline support**: Queue reset request when offline
- **Background timer**: Continue countdown in background
- **State restoration**: Restore progress after app kill
- **A/B testing**: Test different flows and messaging
- **Smart retry**: Exponential backoff for failed requests

---

## 16. Design System Compliance

### 16.1 Color Usage

All colors from `Color.kt`:
- Primary: `HitPromoOrange` (#FF6600)
- Success: `SafetyGreen` (#00C851)
- Error: `AlertRed` (#FF4444)
- Warning: `WarningAmber` (#FFBB33)
- Surface: `SurfacePrimary` (#FFFFFF)
- Background: `IndustrialWhite` (#FAFAFA)

### 16.2 Typography

All text styles from Material3 theme:
- Display: Page titles and branding
- Headline: Section titles
- Title: Card titles and emphasis
- Body: Standard text content
- Label: Small text and captions

### 16.3 Spacing

From `Dimensions.kt`:
- Extra small: 8dp
- Small: 12dp
- Medium: 16dp
- Large: 24dp
- Extra large: 32dp
- Section breaks: 48dp

### 16.4 Elevation

- Cards: 8dp standard
- Buttons: 6dp default, 12dp pressed
- Success overlay: No elevation (flat design)

---

## 17. Approval & Sign-off

### 17.1 Stakeholder Review

- [ ] Product Manager: Approved
- [ ] UX Designer: Approved
- [ ] Android Developer: Implementation feasible
- [ ] QA Lead: Testable requirements
- [ ] Accessibility Expert: WCAG compliant
- [ ] Security Team: Security requirements met

### 17.2 Design Review Checklist

- [ ] Follows industrial design system
- [ ] Touch targets appropriate for gloves
- [ ] High contrast for production floor
- [ ] Consistent with existing screens
- [ ] Accessibility requirements met
- [ ] Error handling comprehensive
- [ ] Loading states defined
- [ ] Success states defined
- [ ] Navigation flow clear
- [ ] Technical feasibility confirmed

---

## Appendix A: Design Assets

### Mockup Links
- [Figma Design File - Coming Soon]
- [Interactive Prototype - Coming Soon]

### Reference Screens
- LoginScreen.kt (reference for layout pattern)
- ForcePasswordChangeScreen.kt (reference for password validation)

### Design System
- IndustrialComponents.kt (component library)
- Color.kt (color palette)
- Dimensions.kt (spacing and sizing)
- AccessibilityGuidelines.kt (accessibility standards)

---

## Appendix B: Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-09-30 | UI Designer Agent | Initial design specification |

---

## Document End

For questions or clarifications, please contact:
- Product Manager
- UI/UX Design Team
- Android Development Team
