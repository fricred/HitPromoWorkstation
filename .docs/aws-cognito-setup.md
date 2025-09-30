# AWS Cognito Setup Guide for HitPromo Workstation

This guide walks you through setting up AWS Cognito User Pool for authentication in the HitPromo Workstation Android application.

## Prerequisites

- AWS Account with appropriate permissions
- AWS CLI installed and configured (optional but recommended)
- Access to AWS Console

## Table of Contents

1. [Create Cognito User Pool](#1-create-cognito-user-pool)
2. [Configure User Pool Settings](#2-configure-user-pool-settings)
3. [Configure Custom Attributes](#3-configure-custom-attributes)
4. [Create App Client](#4-create-app-client)
5. [Configure Identity Pool (Optional)](#5-configure-identity-pool-optional)
6. [Generate Configuration File](#6-generate-configuration-file)
7. [Environment-Specific Configurations](#7-environment-specific-configurations)
8. [Testing](#8-testing)

---

## 1. Create Cognito User Pool

### Via AWS Console

1. Navigate to AWS Cognito in the AWS Console
2. Click **"Create user pool"**
3. Choose **"Email"** as the sign-in option
4. Click **"Next"**

### Via AWS CLI

```bash
aws cognito-idp create-user-pool \
  --pool-name hitpromo-workstation-dev \
  --policies "PasswordPolicy={MinimumLength=8,RequireUppercase=true,RequireLowercase=true,RequireNumbers=true,RequireSymbols=false}" \
  --auto-verified-attributes email \
  --username-attributes email \
  --region us-east-1
```

---

## 2. Configure User Pool Settings

### Password Policy

Configure the following password requirements:
- Minimum length: 8 characters
- Require uppercase letters: Yes
- Require lowercase letters: Yes
- Require numbers: Yes
- Require special characters: No (optional)

### MFA Configuration

For the workstation application:
- **MFA**: Optional (can be enabled per user)
- **MFA Methods**: SMS Text Message, TOTP

### Account Recovery

- **Recovery mechanism**: Email
- **Verified attributes**: Email

### Email Delivery

Choose one of:
- **Default (SES)**: For production environments
- **Amazon SES**: Configure with verified domain/email
- **Cognito Email**: For development (limited to 50 emails/day)

---

## 3. Configure Custom Attributes

The application requires the following custom attributes for role-based access control:

### Required Custom Attributes

| Attribute Name | Type | Mutable | Description |
|---------------|------|---------|-------------|
| `custom:role` | String | Yes | User role (ADMIN, SUPERVISOR, OPERATOR) |
| `custom:is_active` | String | Yes | Account active status ("true"/"false") |
| `custom:last_login` | String | Yes | Timestamp of last successful login |

### Via AWS Console

1. In User Pool Settings, go to **"Sign-up experience"**
2. Scroll to **"Custom attributes"**
3. Click **"Add custom attribute"**
4. Add each attribute:
   - Name: `role` (Cognito will prefix with `custom:`)
   - Type: String
   - Min length: 1
   - Max length: 50
   - Mutable: Yes
5. Repeat for `is_active` and `last_login`

### Via AWS CLI

```bash
aws cognito-idp add-custom-attributes \
  --user-pool-id YOUR_USER_POOL_ID \
  --custom-attributes \
    Name=role,AttributeDataType=String,Mutable=true \
    Name=is_active,AttributeDataType=String,Mutable=true \
    Name=last_login,AttributeDataType=String,Mutable=true
```

---

## 4. Create App Client

The Android app needs an App Client to interact with Cognito.

### Via AWS Console

1. In your User Pool, go to **"App integration"** tab
2. Scroll to **"App clients and analytics"**
3. Click **"Create app client"**
4. Configure:
   - **App client name**: `hitpromo-workstation-android`
   - **App type**: Public client
   - **Authentication flows**:
     - ✅ ALLOW_USER_SRP_AUTH
     - ✅ ALLOW_REFRESH_TOKEN_AUTH
     - ✅ ALLOW_USER_PASSWORD_AUTH (for development only)
   - **Token expiration**:
     - ID token: 60 minutes
     - Access token: 60 minutes
     - Refresh token: 30 days
   - **Prevent user existence errors**: Enabled (recommended)
5. Click **"Create app client"**
6. **Save the App Client ID** - you'll need this for configuration

### Via AWS CLI

```bash
aws cognito-idp create-user-pool-client \
  --user-pool-id YOUR_USER_POOL_ID \
  --client-name hitpromo-workstation-android \
  --no-generate-secret \
  --explicit-auth-flows ALLOW_USER_SRP_AUTH ALLOW_REFRESH_TOKEN_AUTH ALLOW_USER_PASSWORD_AUTH \
  --access-token-validity 60 \
  --id-token-validity 60 \
  --refresh-token-validity 30 \
  --token-validity-units AccessToken=minutes,IdToken=minutes,RefreshToken=days \
  --prevent-user-existence-errors ENABLED
```

---

## 5. Configure Identity Pool (Optional)

If you need to access other AWS services (S3, etc.) from the app:

1. Navigate to **"Federated Identities"** in Cognito
2. Click **"Create new identity pool"**
3. Name it: `hitpromo_workstation_identity_pool`
4. Enable **"Unauthenticated access"**: No
5. Under **"Authentication providers"**:
   - Choose **"Cognito"**
   - Enter your User Pool ID and App Client ID
6. Create the pool
7. Note the **Identity Pool ID** for configuration

### IAM Roles

Configure IAM roles for authenticated users with minimal permissions:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject"
      ],
      "Resource": [
        "arn:aws:s3:::hitpromo-workstation-uploads/${cognito-identity.amazonaws.com:sub}/*"
      ]
    }
  ]
}
```

---

## 6. Generate Configuration File

### Create amplifyconfiguration.json

1. Copy the template from `app/src/main/res/raw/amplifyconfiguration_template.json`
2. Rename it to `amplifyconfiguration.json`
3. Replace the placeholder values:

```json
{
  "UserAgent": "aws-amplify-cli/2.0",
  "Version": "1.0",
  "auth": {
    "plugins": {
      "awsCognitoAuthPlugin": {
        "UserAgent": "aws-amplify-cli/0.1.0",
        "Version": "0.1.0",
        "IdentityManager": {
          "Default": {}
        },
        "CredentialsProvider": {
          "CognitoIdentity": {
            "Default": {
              "PoolId": "us-east-1:12345678-1234-1234-1234-123456789012",
              "Region": "us-east-1"
            }
          }
        },
        "CognitoUserPool": {
          "Default": {
            "PoolId": "us-east-1_ABCD1234",
            "AppClientId": "1234567890abcdefghijklmno",
            "Region": "us-east-1"
          }
        },
        "Auth": {
          "Default": {
            "authenticationFlowType": "USER_SRP_AUTH",
            "socialProviders": [],
            "usernameAttributes": ["EMAIL"],
            "signupAttributes": ["EMAIL"],
            "passwordProtectionSettings": {
              "passwordPolicyMinLength": 8,
              "passwordPolicyCharacters": []
            },
            "mfaConfiguration": "OFF",
            "mfaTypes": ["SMS"],
            "verificationMechanisms": ["EMAIL"]
          }
        }
      }
    }
  }
}
```

### Replace These Values:

- **PoolId** (Identity Pool): Found in Cognito Identity Pools dashboard
- **Region** (Identity Pool): Your AWS region (e.g., `us-east-1`)
- **PoolId** (User Pool): Found in Cognito User Pools dashboard (format: `region_XXXXX`)
- **AppClientId**: Found in your User Pool's App Client settings
- **Region** (User Pool): Your AWS region

### Important Notes:

- **DO NOT commit** `amplifyconfiguration.json` to version control
- Add it to `.gitignore`:
  ```
  # AWS Amplify configuration
  app/src/main/res/raw/amplifyconfiguration.json
  ```
- Use environment-specific configuration files for different environments

---

## 7. Environment-Specific Configurations

### Development Environment

```json
{
  "CognitoUserPool": {
    "Default": {
      "PoolId": "us-east-1_DEVPOOL1",
      "AppClientId": "dev-client-id-12345",
      "Region": "us-east-1"
    }
  }
}
```

### Staging Environment

```json
{
  "CognitoUserPool": {
    "Default": {
      "PoolId": "us-east-1_STGPOOL1",
      "AppClientId": "staging-client-id-67890",
      "Region": "us-east-1"
    }
  }
}
```

### Production Environment

```json
{
  "CognitoUserPool": {
    "Default": {
      "PoolId": "us-east-1_PRODPOOL",
      "AppClientId": "prod-client-id-abcde",
      "Region": "us-east-1"
    }
  }
}
```

### Build Variant Configuration

Update `app/build.gradle.kts` to use different configs per build variant:

```kotlin
android {
    buildTypes {
        debug {
            // Copy dev config during build
            val configFile = file("../configs/amplifyconfiguration.dev.json")
            val targetFile = file("src/main/res/raw/amplifyconfiguration.json")
            copy {
                from(configFile)
                into(targetFile.parent)
                rename { "amplifyconfiguration.json" }
            }
        }

        release {
            // Copy prod config during build
            val configFile = file("../configs/amplifyconfiguration.prod.json")
            val targetFile = file("src/main/res/raw/amplifyconfiguration.json")
            copy {
                from(configFile)
                into(targetFile.parent)
                rename { "amplifyconfiguration.json" }
            }
        }
    }
}
```

---

## 8. Testing

### Create Test Users

#### Via AWS Console

1. Go to your User Pool
2. Click **"Users"** tab
3. Click **"Create user"**
4. Configure:
   - **Username**: Use email format (e.g., `admin@hitpromo.net`)
   - **Email**: Same as username
   - **Temporary password**: Set a temporary password
   - **Email verified**: Mark as verified
5. After creation, set custom attributes:
   - Go to user details
   - Under **"Custom attributes"**
   - Set `custom:role` = `ADMIN`
   - Set `custom:is_active` = `true`

#### Via AWS CLI

```bash
# Create user
aws cognito-idp admin-create-user \
  --user-pool-id YOUR_USER_POOL_ID \
  --username admin@hitpromo.net \
  --user-attributes \
    Name=email,Value=admin@hitpromo.net \
    Name=email_verified,Value=true \
    Name=custom:role,Value=ADMIN \
    Name=custom:is_active,Value=true \
  --temporary-password TempPass123! \
  --message-action SUPPRESS

# Set permanent password
aws cognito-idp admin-set-user-password \
  --user-pool-id YOUR_USER_POOL_ID \
  --username admin@hitpromo.net \
  --password AdminPass123! \
  --permanent
```

### Test User Roles

Create test users for each role:

#### Admin User
```bash
Username: admin@hitpromo.net
Password: AdminPass123!
custom:role = ADMIN
custom:is_active = true
```

#### Supervisor User
```bash
Username: supervisor@hitpromo.net
Password: SuperPass123!
custom:role = SUPERVISOR
custom:is_active = true
```

#### Operator User
```bash
Username: operator@hitpromo.net
Password: OperPass123!
custom:role = OPERATOR
custom:is_active = true
```

### Test Authentication Flow

1. Build and run the Android app
2. Use test credentials to sign in
3. Verify:
   - Sign-in succeeds
   - User attributes are correctly fetched
   - Role is properly mapped
   - Session persists across app restarts
   - Sign-out clears session correctly

### Monitor Authentication

Check CloudWatch logs for Cognito events:
- Sign-in attempts
- Failed authentications
- Token refreshes
- User attribute fetches

---

## Troubleshooting

### Common Issues

#### 1. "User does not exist" error
- Verify the username/email format matches Cognito configuration
- Check that `usernameAttributes` is set to `["EMAIL"]` in configuration

#### 2. "Invalid password format" error
- Verify password meets the password policy requirements
- Check minimum length and character requirements

#### 3. "User is not confirmed" error
- Manually confirm users in AWS Console
- Or mark email as verified during user creation

#### 4. "NotAuthorizedException" error
- Verify the App Client ID is correct
- Check that required auth flows are enabled
- Ensure the user's password is set to permanent (not temporary)

#### 5. Custom attributes not appearing
- Verify attributes are defined in User Pool
- Ensure they're prefixed with `custom:`
- Check that attributes are set for the user

#### 6. Configuration not loading
- Verify `amplifyconfiguration.json` exists in `app/src/main/res/raw/`
- Check JSON syntax is valid
- Ensure file is not named `.template`
- Rebuild the project

### Enable Debug Logging

Add to `HitPromoWorkstationApplication.kt`:

```kotlin
import com.amplifyframework.core.Amplify
import com.amplifyframework.logging.AndroidLoggingPlugin
import com.amplifyframework.logging.LogLevel

// In onCreate, before Amplify.configure():
Amplify.addPlugin(AndroidLoggingPlugin(LogLevel.VERBOSE))
```

---

## Security Best Practices

1. **Never commit** AWS credentials or configuration files to version control
2. **Use environment variables** or secure secret management for sensitive values
3. **Enable MFA** for production users, especially admins
4. **Rotate App Client secrets** regularly
5. **Monitor failed sign-in attempts** and implement account lockout policies
6. **Use least-privilege IAM roles** for Identity Pool
7. **Enable CloudTrail** for audit logging
8. **Regularly review** user permissions and roles
9. **Implement password rotation** policies
10. **Use HTTPS** for all API communications

---

## Additional Resources

- [AWS Amplify Android Documentation](https://docs.amplify.aws/lib/auth/getting-started/q/platform/android/)
- [AWS Cognito User Pools Documentation](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html)
- [AWS Cognito Identity Pools Documentation](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-identity.html)
- [Amplify CLI Documentation](https://docs.amplify.aws/cli/)

---

## Support

For issues specific to HitPromo Workstation authentication:
1. Check application logs in Logcat (filter by "CognitoAuthDataSource" or "HitPromoWorkstation")
2. Review AWS CloudWatch logs for Cognito events
3. Verify configuration against this guide
4. Contact development team with error details and logs