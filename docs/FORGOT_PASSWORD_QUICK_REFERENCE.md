# Forgot Password Flow - Quick Reference Guide

## For Developers & Product Managers

This is a condensed reference for the forgot password flow design. For complete specifications, see `FORGOT_PASSWORD_DESIGN_SPEC.md`.

---

## Flow Summary

**4 Steps:**
1. Request Reset (Enter username/email)
2. Verify Code (Enter 6-digit code from email)
3. Create Password (Set new password with validation)
4. Success (Confirmation and redirect)

**Time to Complete:** ~2-3 minutes
**Code Expiration:** 15 minutes
**Resend Cooldown:** 60 seconds

---

## Layout Pattern

**All screens use the same 40/60 landscape split:**

```
┌────────────────┬─────────────────────────────┐
│                │                             │
│  LEFT PANEL    │       RIGHT PANEL           │
│  (40% width)   │       (60% width)           │
│                │                             │
│  - HP Logo     │  - Screen Title             │
│  - Branding    │  - Form Fields              │
│  - Context     │  - Validation               │
│    Card        │  - Action Buttons           │
│                │                             │
└────────────────┴─────────────────────────────┘
```

Matches `LoginScreen.kt` and `ForcePasswordChangeScreen.kt` pattern.

---

## Component Reuse

### From IndustrialComponents.kt

| Component | Usage |
|-----------|-------|
| `IndustrialButton` | Primary actions (Request Code, Verify Code, Reset Password) |
| `IndustrialSecondaryButton` | Secondary actions (Cancel, Back) |
| `IndustrialTextField` | All text inputs (username, code, passwords) |
| `IndustrialCard` | Information cards in left panel |
| `IndustrialLoadingIndicator` | Loading states between API calls |

### From PasswordStrengthIndicator.kt

| Component | Usage |
|-----------|-------|
| `PasswordStrengthIndicator` | Show strength bar in Step 3 |

### From PasswordRequirementsChecklist.kt

| Component | Usage |
|-----------|-------|
| `PasswordRequirementsChecklist` | Real-time requirement validation in Step 3 |

**No new components needed!** Everything exists in the design system.

---

## Screen-by-Screen Checklist

### Step 1: Request Reset

**Form Fields:**
- Username/Email input (IndustrialTextField)
  - Label: "Username or Email"
  - Keyboard: Email
  - IME: Done
  - Min height: 64dp

**Buttons:**
- Cancel (Secondary, left)
- Request Code (Primary, right, enabled when input not blank)

**Loading:** "Sending verification code..."

**Errors:** Show above buttons, keep form enabled

**API Call:** `authRepository.initiatePasswordReset(usernameOrEmail)`

**Success:** Navigate to Step 2, save username and masked email

---

### Step 2: Verify Code

**Form Fields:**
- Verification code input (IndustrialTextField)
  - Label: "Verification Code"
  - Keyboard: Number
  - Max length: 6 digits
  - IME: Done
  - Auto-submit when 6 digits entered

**Additional UI:**
- Countdown timer: 60 seconds
- Resend button: Enabled after countdown
- Display format: "Resend Code (45s)"

**Buttons:**
- Back (Secondary, left, returns to Step 1)
- Verify Code (Primary, right, enabled when code.length == 6)

**Loading:** "Verifying code..."

**Errors:**
- "Invalid verification code"
- "This code has expired. Please request a new one."
- "Too many failed attempts. Please request a new code."

**API Call:** Part of `confirmPasswordReset` (combined with Step 3 in Cognito)

**Success:** Navigate to Step 3

---

### Step 3: Create New Password

**Form Fields:**
- New password (IndustrialTextField with visibility toggle)
  - Label: "New Password"
  - IME: Next
  - Toggle icon: 48dp tap area

- Confirm password (IndustrialTextField with visibility toggle)
  - Label: "Confirm Password"
  - IME: Done
  - Error if not matching

**Validation UI:**
- PasswordStrengthIndicator (when newPassword not empty)
- PasswordRequirementsChecklist (always visible)
  - 8+ characters
  - Uppercase (A-Z)
  - Lowercase (a-z)
  - Number (0-9)
  - Special character
  - Passwords match

**Buttons:**
- Back (Secondary, left, returns to Step 2)
- Reset Password (Primary, right, enabled when all requirements met)

**Loading:** "Resetting password..."

**Errors:**
- "Password does not meet requirements"
- "Failed to reset password. Please try again."

**API Call:** `authRepository.confirmPasswordReset(username, code, newPassword)`

**Success:** Show Step 4 overlay

---

### Step 4: Success

**Display:** Full-screen overlay with:
- Success icon (120dp circle, checkmark)
- Title: "Password Reset Successful!"
- Message: "You can now sign in with your new password."
- Redirect notice: "Redirecting to login..."
- Optional button: "Return to Login"

**Behavior:**
- Auto-dismiss after 2.5 seconds
- Clear all state
- Navigate to LoginScreen
- Optional: Pre-fill username

---

## State Management

```kotlin
data class ForgotPasswordState(
    // Navigation
    val currentStep: ForgotPasswordStep = REQUEST_RESET,

    // Step 1
    val usernameOrEmail: String = "",

    // Step 2
    val verificationCode: String = "",
    val maskedEmail: String = "",
    val resendCountdown: Int = 60,
    val canResendCode: Boolean = false,

    // Step 3
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val passwordStrength: PasswordStrength = WEAK,
    val passwordsMatch: Boolean = false,
    val allRequirementsMet: Boolean = false,

    // Common
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successTimestamp: Long? = null
)
```

---

## API Integration (AWS Cognito)

### Step 1: Initiate Reset

```kotlin
// Call
val result = Amplify.Auth.resetPassword(usernameOrEmail)

// Returns
result.destination // e.g., "u***@example.com"

// Exceptions
- UserNotFoundException → Generic success message (security)
- LimitExceededException → "Too many attempts"
```

### Step 2 + 3: Verify Code + Reset Password

```kotlin
// Call (combined in Cognito)
Amplify.Auth.confirmResetPassword(
    username = usernameOrEmail,
    newPassword = newPassword,
    confirmationCode = verificationCode
)

// Returns
Success (Unit)

// Exceptions
- CodeMismatchException → "Invalid code"
- ExpiredCodeException → "Code expired"
- InvalidPasswordException → "Password weak"
```

### Resend Code

```kotlin
// Call
val result = Amplify.Auth.resetPassword(usernameOrEmail)

// Same as initial reset request
```

---

## Touch Target Requirements

**All interactive elements:**
- Minimum: 48dp height
- Preferred: 64dp height (for gloved operation)
- Spacing: 16dp between targets

**Specific requirements:**
- Buttons: 64dp height
- Text fields: 64dp height
- Icon buttons: 48dp tap area
- Visibility toggles: 48dp tap area

---

## Accessibility

**Semantic descriptions:**
```kotlin
IndustrialTextField(
    modifier = Modifier.semantics {
        contentDescription = "Verification code input, enter 6 digits"
    }
)

IndustrialButton(
    modifier = Modifier.semantics {
        contentDescription = "Reset password and return to login"
    }
)
```

**Screen reader announcements:**
- Error messages: Automatically announced
- Loading states: "Loading, [message]"
- Success: "Password reset successful"
- Step progress: "Step 1 of 3, Request reset"

**Keyboard navigation:**
- Tab order: Top to bottom
- IME actions: Next → Next → Done
- Enter key: Submits form
- Escape: Triggers cancel

---

## Error Handling Matrix

| Error Type | User Message | Action |
|------------|-------------|--------|
| No network | "Network unavailable. Please check your connection." | Allow retry |
| Invalid input | Inline validation with red border | Prevent submit |
| User not found | "If an account exists, a code has been sent." | Continue (security) |
| Invalid code | "Invalid verification code. Please try again." | Allow retry |
| Expired code | "This code has expired. Please request a new one." | Offer resend |
| Weak password | Inline via requirements checklist | Prevent submit |
| Too many attempts | "Too many attempts. Please try again later." | Block temporarily |
| Server error | "An error occurred. Please try again." | Allow retry |
| Timeout | "Request timeout. Please try again." | Allow retry |

---

## Animation Specs

| Element | Duration | Type |
|---------|----------|------|
| Screen transitions | 300ms | Slide + Fade |
| Password strength bar | 300ms | Width change |
| Checklist checkmarks | 150ms | Fade in |
| Error messages | 200ms | Slide from top |
| Success overlay | 300ms | Fade in |
| Success icon | 400ms | Scale + Fade |
| Button states | 200ms | Color crossfade |
| Loading spinner | Continuous | Rotate |

---

## Testing Priorities

### Critical Path
1. Complete flow with valid data
2. Code verification success/failure
3. Password validation requirements
4. Network error handling
5. Session timeout handling

### Edge Cases
1. Very long username/email
2. Multiple rapid resend requests
3. App backgrounded during flow
4. Code expires while on screen
5. Weak password attempts

### Accessibility
1. Touch targets minimum 64dp
2. Screen reader compatibility
3. Keyboard navigation
4. High contrast mode
5. Text scaling to 200%

### Device Testing
1. Samsung Galaxy Tab A9+ (primary)
2. Various tablet sizes
3. Different Android versions
4. Bright lighting conditions
5. Gloved finger interaction

---

## File Structure

```
app/src/main/java/net/hitpromo/hitpromoworkstation/
├── ui/screens/
│   └── ForgotPasswordScreen.kt
├── presentation/forgotpassword/
│   ├── ForgotPasswordViewModel.kt
│   ├── ForgotPasswordState.kt
│   └── ForgotPasswordIntent.kt
├── domain/usecase/
│   ├── ResetPasswordUseCase.kt
│   ├── VerifyResetCodeUseCase.kt
│   └── ConfirmPasswordResetUseCase.kt
└── data/repository/
    └── AuthRepository.kt (extend existing)
```

---

## Color Palette Reference

| Usage | Color | Hex |
|-------|-------|-----|
| Primary | HitPromoOrange | #FF6600 |
| Success | SafetyGreen | #00C851 |
| Error | AlertRed | #FF4444 |
| Warning | WarningAmber | #FFBB33 |
| Info | InfoBlue | #0099FF |
| Surface | SurfacePrimary | #FFFFFF |
| Background | IndustrialWhite | #FAFAFA |

---

## Typography Scale

| Usage | Style | Size |
|-------|-------|------|
| Page title | headlineMedium | ~28sp |
| Section title | titleLarge | ~22sp |
| Card title | titleMedium | ~16sp |
| Body text | bodyLarge | ~16sp |
| Helper text | bodyMedium | ~14sp |
| Button text | titleMedium | ~16sp |

---

## Common Pitfalls to Avoid

1. **Don't** reveal if user exists (security risk)
   - Always show generic success message in Step 1

2. **Don't** clear form data on error
   - Keep user input, allow correction

3. **Don't** allow submission with unmet requirements
   - Disable button, show validation errors

4. **Don't** forget the countdown timer
   - Required for resend rate limiting

5. **Don't** lose state on screen rotation
   - Implement proper state preservation

6. **Don't** skip loading states
   - Always show feedback during API calls

7. **Don't** ignore accessibility
   - Add semantic descriptions to all UI

8. **Don't** use hardcoded strings
   - Use string resources for localization

---

## Implementation Estimate

**Complexity:** Medium
**Estimated Effort:** 8-12 hours

**Breakdown:**
- UI implementation: 4-5 hours
- ViewModel & state: 2-3 hours
- API integration: 2-3 hours
- Testing: 2-3 hours
- Polish & bug fixes: 1-2 hours

**Dependencies:**
- AWS Cognito setup complete
- Design system components available
- Authentication repository exists

---

## Key Decisions Summary

1. **4-step flow** matches Cognito's reset process
2. **40/60 split layout** consistent with login screen
3. **Reuse existing components** from design system
4. **Combined code verification** with password reset (Cognito limitation)
5. **60-second cooldown** prevents spam
6. **Generic error messages** for security
7. **Real-time validation** for better UX
8. **Auto-redirect** after success (2.5 seconds)

---

## Quick Links

- Full Design Spec: `FORGOT_PASSWORD_DESIGN_SPEC.md`
- Flow Diagrams: `FORGOT_PASSWORD_FLOW_DIAGRAM.md`
- Component Library: `app/src/main/java/.../ui/components/IndustrialComponents.kt`
- Login Screen Reference: `app/src/main/java/.../ui/screens/LoginScreen.kt`
- Force Password Change Reference: `app/src/main/java/.../ui/screens/ForcePasswordChangeScreen.kt`

---

## Questions?

Contact:
- **Design Questions**: UI/UX Design Team
- **Technical Questions**: Android Development Team
- **Business Logic**: Product Manager
- **Security Concerns**: Security Team

---

## Document Version

**Version:** 1.0
**Date:** 2025-09-30
**Status:** Ready for Implementation
