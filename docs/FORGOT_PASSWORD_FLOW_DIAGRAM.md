# Forgot Password Flow - Visual Diagrams

## User Flow Diagram

```
                    ┌─────────────────────┐
                    │   Login Screen      │
                    │                     │
                    │  [Username]         │
                    │  [Password]         │
                    │  [ Sign In ]        │
                    │                     │
                    │  [Forgot Password?]◄───── Entry Point
                    └──────────┬──────────┘
                               │
                               ▼
        ╔═══════════════════════════════════════════════╗
        ║  STEP 1: REQUEST PASSWORD RESET               ║
        ╟───────────────────────────────────────────────╢
        ║  Left Panel:         Right Panel:             ║
        ║  ┌─────────────┐    ┌──────────────────────┐ ║
        ║  │ [HP Logo]   │    │ Forgot Your Password?│ ║
        ║  │ Hit Promo   │    │                      │ ║
        ║  │ Workstation │    │ [Username/Email]     │ ║
        ║  │             │    │                      │ ║
        ║  │ ┌─────────┐ │    │ [Cancel][Request]    │ ║
        ║  │ │Password │ │    │                      │ ║
        ║  │ │Reset    │ │    └──────────────────────┘ ║
        ║  │ │Info     │ │                             ║
        ║  │ └─────────┘ │                             ║
        ║  └─────────────┘                             ║
        ╚═══════════════════════════════════════════════╝
                               │
                    ┌──────────┴──────────┐
                    │                     │
              Valid Input           Invalid Input
                    │                     │
                    ▼                     ▼
            [API Call to AWS]       [Show Error]
            [Cognito resetPassword]  [Keep Form Open]
                    │
        ┌───────────┴───────────┐
        │                       │
   User Exists           User Not Found
        │                       │
        │                       └──► [Generic Success Message]
        │                            (Security: Don't reveal)
        ▼
  [Code Sent Successfully]
        │
        ▼
        ╔═══════════════════════════════════════════════╗
        ║  STEP 2: VERIFY CODE                          ║
        ╟───────────────────────────────────────────────╢
        ║  Left Panel:         Right Panel:             ║
        ║  ┌─────────────┐    ┌──────────────────────┐ ║
        ║  │ [HP Logo]   │    │ Enter Verification   │ ║
        ║  │ Hit Promo   │    │ Code                 │ ║
        ║  │ Workstation │    │                      │ ║
        ║  │             │    │ [6-Digit Code]       │ ║
        ║  │ ┌─────────┐ │    │                      │ ║
        ║  │ │Code Sent│ │    │ Didn't receive?      │ ║
        ║  │ │to:      │ │    │ [Resend] (60s timer) │ ║
        ║  │ │u***@*.com│ │   │                      │ ║
        ║  │ │Expires: │ │    │ [Back][Verify Code]  │ ║
        ║  │ │15 min   │ │    │                      │ ║
        ║  │ └─────────┘ │    └──────────────────────┘ ║
        ║  └─────────────┘                             ║
        ╚═══════════════════════════════════════════════╝
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
   [Resend Code]          [Back Button]        [Verify Code]
        │                      │                      │
        │                      ▼                      │
        │              [Return to Step 1]             │
        │              (Keep Username)                │
        │                                             │
        └──► [New Code Sent]                         │
             [Reset Timer]                           │
                                                     │
                                            ┌────────┴────────┐
                                            │                 │
                                      Valid Code        Invalid Code
                                            │                 │
                                            │                 ▼
                                            │         [Show Error Message]
                                            │         ┌──────────────────┐
                                            │         │ - Invalid Code   │
                                            │         │ - Expired Code   │
                                            │         │ - Too Many Tries │
                                            │         └──────────────────┘
                                            │                 │
                                            │                 └──► [Allow Retry]
                                            │                      [Offer Resend]
                                            ▼
        ╔═══════════════════════════════════════════════╗
        ║  STEP 3: CREATE NEW PASSWORD                  ║
        ╟───────────────────────────────────────────────╢
        ║  Left Panel:         Right Panel:             ║
        ║  ┌─────────────┐    ┌──────────────────────┐ ║
        ║  │ [HP Logo]   │    │ Set Your New         │ ║
        ║  │ Hit Promo   │    │ Password             │ ║
        ║  │ Workstation │    │                      │ ║
        ║  │             │    │ [New Password]   👁  │ ║
        ║  │ ┌─────────┐ │    │ [Confirm Pass]   👁  │ ║
        ║  │ │Create   │ │    │                      │ ║
        ║  │ │New      │ │    │ Strength: [====  ]   │ ║
        ║  │ │Password │ │    │                      │ ║
        ║  │ │         │ │    │ Requirements:        │ ║
        ║  │ │Must be  │ │    │ ✓ 8+ characters     │ ║
        ║  │ │different│ │    │ ✓ Uppercase (A-Z)   │ ║
        ║  │ │from     │ │    │ ○ Lowercase (a-z)   │ ║
        ║  │ │previous │ │    │ ○ Number (0-9)      │ ║
        ║  │ └─────────┘ │    │ ○ Special (!@#$)    │ ║
        ║  └─────────────┘    │ ○ Passwords match   │ ║
        ║                     │                      │ ║
        ║                     │ [Back][Reset Pass]   │ ║
        ║                     └──────────────────────┘ ║
        ╚═══════════════════════════════════════════════╝
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
   [Back Button]       [Requirements Met?]    [Reset Password]
        │                      │                      │
        ▼                      │                      │
 [Return to Step 2]            │                      │
 (Code Still Valid)            │                      │
                        ┌──────┴──────┐               │
                        │             │               │
                       NO            YES              │
                        │             │               │
                        │             └───────────────┘
                        │                             │
                        ▼                             ▼
              [Disable Button]              [API Call to AWS]
              [Show Errors]                 [Cognito confirmResetPassword]
                                                      │
                                            ┌─────────┴─────────┐
                                            │                   │
                                        Success             Failure
                                            │                   │
                                            │                   ▼
                                            │           [Show Error]
                                            │           ┌──────────────────┐
                                            │           │ - Password Weak  │
                                            │           │ - Password Reused│
                                            │           │ - Network Error  │
                                            │           └──────────────────┘
                                            │                   │
                                            │                   └──► [Allow Retry]
                                            ▼
        ╔═══════════════════════════════════════════════╗
        ║  STEP 4: SUCCESS CONFIRMATION                 ║
        ╟───────────────────────────────────────────────╢
        ║              (Full Screen Overlay)            ║
        ║                                               ║
        ║                                               ║
        ║               ┌───────────┐                   ║
        ║               │     ✓     │                   ║
        ║               │  Success  │                   ║
        ║               └───────────┘                   ║
        ║                                               ║
        ║         Password Reset Successful!            ║
        ║                                               ║
        ║     You can now sign in with your new         ║
        ║     password.                                 ║
        ║                                               ║
        ║     Redirecting to login... (3s timer)        ║
        ║                                               ║
        ║         ┌───────────────────────┐             ║
        ║         │  Return to Login      │             ║
        ║         └───────────────────────┘             ║
        ║                                               ║
        ╚═══════════════════════════════════════════════╝
                               │
                               │ (Auto after 2.5s)
                               │ (Or manual button click)
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Login Screen      │
                    │                     │
                    │  [Username]         │◄─── Optional: Pre-fill username
                    │  [Password]         │
                    │  [ Sign In ]        │
                    │                     │
                    └─────────────────────┘
```

---

## State Transition Diagram

```
                         ┌──────────────────┐
                         │  LOGIN_SCREEN    │
                         └────────┬─────────┘
                                  │
                                  │ [Forgot Password Click]
                                  │
                                  ▼
                  ┌───────────────────────────────┐
                  │  REQUEST_RESET                │
                  │                               │
                  │  State:                       │
                  │  - usernameOrEmail: ""        │
                  │  - isLoading: false           │
                  │  - errorMessage: null         │
                  └───────────────────────────────┘
                                  │
                ┌─────────────────┼─────────────────┐
                │                                   │
           [Cancel Click]                [Request Code Click]
                │                                   │
                ▼                                   ▼
         [Show Confirm]                   [Set isLoading: true]
         [Clear State]                    [Call API]
         [Return to Login]                        │
                                        ┌─────────┴─────────┐
                                        │                   │
                                    Success             Failure
                                        │                   │
                                        ▼                   ▼
                              [Set isLoading: false] [Set errorMessage]
                              [Store username]       [Set isLoading: false]
                              [Store maskedEmail]    [Stay on step]
                              [Navigate to next]           │
                                        │                   │
                                        ▼                   │
                  ┌───────────────────────────────┐        │
                  │  VERIFY_CODE                  │        │
                  │                               │        │
                  │  State:                       │        │
                  │  - usernameOrEmail: "saved"   │        │
                  │  - verificationCode: ""       │        │
                  │  - maskedEmail: "u***@*.com"  │        │
                  │  - resendCountdown: 60        │        │
                  │  - canResendCode: false       │        │
                  │  - isLoading: false           │        │
                  └───────────────────────────────┘        │
                                  │                         │
                ┌─────────────────┼─────────────────┐      │
                │                 │                 │      │
          [Back Click]    [Resend Code Click] [Verify Click]
                │                 │                 │      │
                ▼                 ▼                 ▼      │
         [Return to       [Call API]        [Validate]    │
          Step 1]         [Reset Timer]     [Call API]    │
         [Keep data]               │              │       │
                                   ▼              ▼       │
                         [New Code Sent]  [Check Result] │
                                                  │       │
                                        ┌─────────┴─────────┐
                                        │                   │
                                    Success             Failure
                                        │                   │
                                        ▼                   ▼
                              [Set isLoading: false] [Set errorMessage]
                              [Navigate to next]     [Stay on step]
                                        │                   │
                                        ▼                   │
                  ┌───────────────────────────────┐        │
                  │  CREATE_PASSWORD              │        │
                  │                               │        │
                  │  State:                       │        │
                  │  - newPassword: ""            │        │
                  │  - confirmPassword: ""        │        │
                  │  - isNewPasswordVisible: false│        │
                  │  - isConfirmPasswordVisible   │        │
                  │  - passwordStrength: WEAK     │        │
                  │  - passwordsMatch: false      │        │
                  │  - allRequirementsMet: false  │        │
                  │  - isLoading: false           │        │
                  └───────────────────────────────┘        │
                                  │                         │
                ┌─────────────────┼─────────────────┐      │
                │                 │                 │      │
          [Back Click]    [Password Change]  [Reset Click] │
                │                 │                 │      │
                ▼                 ▼                 ▼      │
         [Return to      [Update State]     [Validate]    │
          Step 2]        [Validate]         [Call API]    │
                         [Check Requirements]     │       │
                                                  ▼       │
                                          [Check Result]  │
                                                  │       │
                                        ┌─────────┴─────────┐
                                        │                   │
                                    Success             Failure
                                        │                   │
                                        ▼                   ▼
                              [Set successTimestamp]  [Set errorMessage]
                              [Navigate to success]   [Stay on step]
                                        │                   │
                                        ▼                   │
                  ┌───────────────────────────────┐        │
                  │  SUCCESS                      │        │
                  │                               │        │
                  │  State:                       │        │
                  │  - successTimestamp: [time]   │        │
                  │  - showSuccessOverlay: true   │        │
                  │                               │        │
                  │  Auto-redirect after 2.5s     │        │
                  └───────────────────────────────┘        │
                                  │                         │
                ┌─────────────────┼─────────────────┐      │
                │                                   │      │
        [Auto Timer Expires]            [Manual Button]    │
                │                                   │      │
                ▼                                   ▼      │
         [Clear All State]                  [Clear All State]
         [Navigate to Login]                [Navigate to Login]
                │                                   │      │
                └───────────────┬───────────────────┘      │
                                ▼                          │
                         ┌──────────────────┐              │
                         │  LOGIN_SCREEN    │              │
                         │  (Pre-filled)    │              │
                         └──────────────────┘              │
                                                            │
                        [Cancel/Error Paths] ◄──────────────┘
                                │
                                ▼
                         [Show Confirmation]
                         [Clear State]
                         [Return to Login]
```

---

## Component Hierarchy Diagram

```
ForgotPasswordScreen
│
├── Box (Root Container)
│   │
│   ├── when (currentStep)
│   │   │
│   │   ├── REQUEST_RESET → RequestResetStep
│   │   │   │
│   │   │   ├── Row (Horizontal Split)
│   │   │   │   │
│   │   │   │   ├── BrandingPanel (40% weight)
│   │   │   │   │   ├── Surface (primaryContainer)
│   │   │   │   │   │   └── Column
│   │   │   │   │   │       ├── CompanyLogo (120dp circle)
│   │   │   │   │   │       ├── Text (Company name)
│   │   │   │   │   │       ├── Text (Workstation label)
│   │   │   │   │   │       └── IndustrialCard
│   │   │   │   │   │           └── Column
│   │   │   │   │   │               ├── Text (Title)
│   │   │   │   │   │               └── Text (Instructions)
│   │   │   │   │
│   │   │   │   └── FormPanel (60% weight)
│   │   │   │       └── Column (Centered, scrollable)
│   │   │   │           ├── Text (Title)
│   │   │   │           ├── IndustrialTextField (Username/Email)
│   │   │   │           ├── HelpText (with icon)
│   │   │   │           ├── if (isLoading) IndustrialLoadingIndicator
│   │   │   │           ├── if (errorMessage) ErrorText
│   │   │   │           └── Row (Action buttons)
│   │   │   │               ├── IndustrialSecondaryButton (Cancel)
│   │   │   │               └── IndustrialButton (Request Code)
│   │   │
│   │   ├── VERIFY_CODE → VerifyCodeStep
│   │   │   │
│   │   │   ├── Row (Horizontal Split)
│   │   │   │   │
│   │   │   │   ├── BrandingPanel (40% weight)
│   │   │   │   │   └── Similar structure to above
│   │   │   │   │       └── IndustrialCard
│   │   │   │   │           └── Column
│   │   │   │   │               ├── Text (Code sent title)
│   │   │   │   │               ├── Text (Masked email)
│   │   │   │   │               └── Text (Expiration notice)
│   │   │   │   │
│   │   │   │   └── FormPanel (60% weight)
│   │   │   │       └── Column (Centered, scrollable)
│   │   │   │           ├── Text (Title)
│   │   │   │           ├── IndustrialTextField (Code)
│   │   │   │           ├── HelpText
│   │   │   │           ├── ResendSection
│   │   │   │           │   ├── Text (Didn't receive?)
│   │   │   │           │   └── TextButton (Resend with countdown)
│   │   │   │           ├── if (isLoading) IndustrialLoadingIndicator
│   │   │   │           ├── if (errorMessage) ErrorText
│   │   │   │           └── Row (Action buttons)
│   │   │   │               ├── IndustrialSecondaryButton (Back)
│   │   │   │               └── IndustrialButton (Verify)
│   │   │
│   │   ├── CREATE_PASSWORD → CreatePasswordStep
│   │   │   │
│   │   │   ├── Row (Horizontal Split)
│   │   │   │   │
│   │   │   │   ├── BrandingPanel (40% weight)
│   │   │   │   │   └── Similar structure to above
│   │   │   │   │       └── IndustrialCard
│   │   │   │   │           └── Column
│   │   │   │   │               ├── Text (Create password title)
│   │   │   │   │               └── Text (Instructions)
│   │   │   │   │
│   │   │   │   └── FormPanel (60% weight)
│   │   │   │       └── Column (Centered, scrollable)
│   │   │   │           ├── Text (Title)
│   │   │   │           ├── IndustrialTextField (New password)
│   │   │   │           │   └── trailingIcon: IconButton (Visibility)
│   │   │   │           ├── IndustrialTextField (Confirm password)
│   │   │   │           │   └── trailingIcon: IconButton (Visibility)
│   │   │   │           ├── if (newPassword.isNotEmpty())
│   │   │   │           │   └── PasswordStrengthIndicator
│   │   │   │           ├── PasswordRequirementsChecklist
│   │   │   │           │   └── Column
│   │   │   │           │       ├── Text (Title)
│   │   │   │           │       └── for each requirement
│   │   │   │           │           └── RequirementItem
│   │   │   │           │               ├── Icon (✓ or ○)
│   │   │   │           │               └── Text (Requirement)
│   │   │   │           ├── if (isLoading) IndustrialLoadingIndicator
│   │   │   │           ├── if (errorMessage) ErrorText
│   │   │   │           └── Row (Action buttons)
│   │   │   │               ├── IndustrialSecondaryButton (Back)
│   │   │   │               └── IndustrialButton (Reset Password)
│   │   │
│   │   └── SUCCESS → SuccessOverlay
│   │       │
│   │       └── Box (Full screen overlay)
│   │           └── Column (Centered)
│   │               ├── Surface (Success icon container)
│   │               │   └── Text (✓ checkmark, 72sp)
│   │               ├── Text (Success title)
│   │               ├── Text (Success message)
│   │               ├── Text (Redirecting message)
│   │               └── IndustrialButton (Return to login)
```

---

## Data Flow Diagram

```
┌────────────────────────────────────────────────────────────────┐
│                        USER INTERACTIONS                        │
└────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌────────────────────────────────────────────────────────────────┐
│                      ForgotPasswordScreen                       │
│                          (Composable)                           │
└────────────────────┬───────────────────────────────────────────┘
                     │
                     │ collectAsStateWithLifecycle()
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│                   ForgotPasswordViewModel                       │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │ private val _uiState = MutableStateFlow()               │ │
│  │ val uiState: StateFlow<ForgotPasswordState>             │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                                 │
│  fun handleIntent(intent: ForgotPasswordIntent) {              │
│      when (intent) {                                           │
│          UpdateUsername → updateUsername()                     │
│          RequestReset → requestReset()                         │
│          UpdateCode → updateCode()                             │
│          VerifyCode → verifyCode()                             │
│          ResendCode → resendCode()                             │
│          UpdateNewPassword → updateNewPassword()               │
│          UpdateConfirmPassword → updateConfirmPassword()       │
│          ConfirmPasswordReset → confirmReset()                 │
│          NavigateBack → navigateBack()                         │
│      }                                                          │
│  }                                                              │
└────────────────────┬───────────────────────────────────────────┘
                     │
                     │ Calls use cases
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│                         USE CASES                               │
│                                                                 │
│  ┌───────────────────────────────────────────────────────┐    │
│  │ ResetPasswordUseCase                                  │    │
│  │  - Initiates reset flow                              │    │
│  │  - Returns masked email destination                  │    │
│  └───────────────────────────────────────────────────────┘    │
│                                                                 │
│  ┌───────────────────────────────────────────────────────┐    │
│  │ VerifyResetCodeUseCase                                │    │
│  │  - Validates verification code                        │    │
│  │  - Prepares for password reset                        │    │
│  └───────────────────────────────────────────────────────┘    │
│                                                                 │
│  ┌───────────────────────────────────────────────────────┐    │
│  │ ConfirmPasswordResetUseCase                           │    │
│  │  - Confirms code + sets new password                  │    │
│  │  - Validates password requirements                    │    │
│  └───────────────────────────────────────────────────────┘    │
└────────────────────┬───────────────────────────────────────────┘
                     │
                     │ Uses repository
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│                       AuthRepository                            │
│                                                                 │
│  suspend fun initiatePasswordReset(                            │
│      usernameOrEmail: String                                   │
│  ): AuthResult<String>                                         │
│                                                                 │
│  suspend fun confirmPasswordReset(                             │
│      usernameOrEmail: String,                                  │
│      code: String,                                             │
│      newPassword: String                                       │
│  ): AuthResult<Unit>                                           │
│                                                                 │
│  suspend fun resendPasswordResetCode(                          │
│      usernameOrEmail: String                                   │
│  ): AuthResult<String>                                         │
└────────────────────┬───────────────────────────────────────────┘
                     │
                     │ Calls data source
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│                   CognitoAuthDataSource                         │
│                                                                 │
│  suspend fun resetPassword(                                    │
│      usernameOrEmail: String                                   │
│  ): Result<String> {                                           │
│      val result = Amplify.Auth.resetPassword(usernameOrEmail)  │
│      return Result.success(result.destination)                 │
│  }                                                              │
│                                                                 │
│  suspend fun confirmResetPassword(                             │
│      username: String,                                         │
│      code: String,                                             │
│      newPassword: String                                       │
│  ): Result<Unit> {                                             │
│      Amplify.Auth.confirmResetPassword(                        │
│          username, newPassword, code                           │
│      )                                                          │
│      return Result.success(Unit)                               │
│  }                                                              │
└────────────────────┬───────────────────────────────────────────┘
                     │
                     │ Network calls
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│                      AWS Amplify SDK                            │
└────────────────────┬───────────────────────────────────────────┘
                     │
                     │ HTTPS
                     │
                     ▼
┌────────────────────────────────────────────────────────────────┐
│                      AWS Cognito                                │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ User Pool                                              │   │
│  │  - Validates username/email                           │   │
│  │  - Generates verification code                        │   │
│  │  - Sends email with code                              │   │
│  │  - Validates code on confirmation                     │   │
│  │  - Updates user password                              │   │
│  └────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────┘
                                │
                                │
                                ▼
                        ┌───────────────┐
                        │  AWS SES      │
                        │  (Email)      │
                        └───────────────┘
                                │
                                ▼
                          User's Email
```

---

## Error Handling Flow

```
                    ┌─────────────────────┐
                    │   User Action       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │  Validate Input     │
                    └──────────┬──────────┘
                               │
                    ┌──────────┴──────────┐
                    │                     │
              Input Valid          Input Invalid
                    │                     │
                    ▼                     ▼
         ┌─────────────────┐    ┌──────────────────┐
         │  Call API       │    │  Show Inline     │
         └────────┬────────┘    │  Error           │
                  │             │  - Red border    │
                  │             │  - Error text    │
        ┌─────────┴─────────┐   │  - Keep form     │
        │                   │   └──────────────────┘
   Network OK         Network Error
        │                   │
        ▼                   ▼
┌─────────────┐   ┌──────────────────┐
│ API Call    │   │  Show Error:     │
│             │   │  "Network        │
│             │   │   unavailable"   │
└──────┬──────┘   └──────────────────┘
       │
       │
┌──────┴──────────────────────────┐
│                                 │
│  API Response                   │
│                                 │
├─────────┬──────────┬────────────┤
│         │          │            │
Success   Client    Server    Timeout
          Error     Error
│         │          │            │
▼         ▼          ▼            ▼
Process   │          │     ┌──────────────┐
Success   │          │     │ Show:        │
          │          │     │ "Request     │
          │          │     │  timeout"    │
          │          │     │ [Retry]      │
          │          │     └──────────────┘
          │          │
          ▼          ▼
  ┌──────────────────────────────┐
  │  Parse Error Message         │
  └──────────┬───────────────────┘
             │
    ┌────────┴────────┐
    │                 │
Known Error    Unknown Error
    │                 │
    ▼                 ▼
┌─────────────┐  ┌─────────────┐
│ Map to User │  │ Generic     │
│ Friendly    │  │ Error       │
│ Message:    │  │ Message     │
│             │  │             │
│ - User Not  │  │ "An error   │
│   Found →   │  │  occurred.  │
│   Generic   │  │  Please try │
│                 │  again."    │
│ - Invalid   │  └─────────────┘
│   Code →    │
│   "Invalid  │
│    code"    │
│             │
│ - Expired   │
│   Code →    │
│   "Code has │
│    expired" │
│             │
│ - Weak      │
│   Password →│
│   "Password │
│    too weak"│
└─────────────┘
       │
       ▼
┌──────────────────┐
│ Display Error    │
│ - Alert color    │
│ - Icon           │
│ - Message        │
│ - Action button  │
│   (Retry/Resend) │
└──────────────────┘
       │
       ▼
┌──────────────────┐
│ Keep Form State  │
│ - Don't clear    │
│ - Allow edit     │
│ - Enable retry   │
└──────────────────┘
```

---

## Timer & Countdown Flow

```
          Step 2: Verify Code Screen Loads
                        │
                        ▼
          ┌─────────────────────────────┐
          │ Initialize Countdown Timer  │
          │ - Start at 60 seconds       │
          │ - canResendCode = false     │
          └──────────────┬──────────────┘
                         │
                         ▼
          ┌─────────────────────────────┐
          │ LaunchedEffect with delay   │
          │                             │
          │ while (countdown > 0) {     │
          │     delay(1000)             │
          │     countdown--             │
          │     update UI               │
          │ }                           │
          └──────────────┬──────────────┘
                         │
                         │ Every second
                         ▼
          ┌─────────────────────────────┐
          │ Update Display              │
          │                             │
          │ "Resend Code (45s)"         │
          │ "Resend Code (30s)"         │
          │ "Resend Code (15s)"         │
          │ "Resend Code (5s)"          │
          └──────────────┬──────────────┘
                         │
                         │ countdown == 0
                         ▼
          ┌─────────────────────────────┐
          │ Enable Resend Button        │
          │ - canResendCode = true      │
          │ - Text: "Resend Code"       │
          │ - Button enabled            │
          └──────────────┬──────────────┘
                         │
            ┌────────────┴────────────┐
            │                         │
     User Clicks              User Verifies Code
     "Resend Code"            Successfully
            │                         │
            ▼                         ▼
  ┌─────────────────┐      ┌──────────────────┐
  │ Call API        │      │ Navigate to      │
  │ - Show loading  │      │ Next Step        │
  │ - Disable button│      │ - Clear timer    │
  └────────┬────────┘      └──────────────────┘
           │
    ┌──────┴──────┐
    │             │
 Success       Failure
    │             │
    ▼             ▼
┌─────────┐   ┌────────────┐
│ Reset   │   │ Show Error │
│ Timer   │   │ Keep timer │
│ to 60s  │   └────────────┘
│         │
│ Start   │
│ Count   │
│ Down    │
└─────────┘
```

---

## Document End
