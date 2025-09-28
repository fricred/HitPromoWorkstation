package net.hitpromo.hitpromoworkstation.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import net.hitpromo.hitpromoworkstation.ui.theme.HitPromoWorkstationTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for LoginScreen.
 *
 * Tests the login screen components, interactions, and states
 * on the actual Android UI.
 */
@HiltAndroidTest
class LoginScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun loginScreen_displaysCorrectContent() {
        // Given
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onForgotPasswordClick = { },
                    isLoading = false,
                    errorMessage = null
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Hit Promotional Products").assertIsDisplayed()
        composeTestRule.onNodeWithText("Industrial Workstation").assertIsDisplayed()
        composeTestRule.onNodeWithText("Workstation Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Demo Credentials").assertIsDisplayed()
    }

    @Test
    fun loginScreen_signInButtonDisabledWhenFieldsEmpty() {
        // Given
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onForgotPasswordClick = { },
                    isLoading = false,
                    errorMessage = null
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Sign In").assertIsNotEnabled()
    }

    @Test
    fun loginScreen_signInButtonEnabledWhenFieldsFilled() {
        // Given
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onForgotPasswordClick = { },
                    isLoading = false,
                    errorMessage = null
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Then
        composeTestRule.onNodeWithText("Sign In").assertIsEnabled()
    }

    @Test
    fun loginScreen_displaysLoadingState() {
        // Given
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onForgotPasswordClick = { },
                    isLoading = true,
                    errorMessage = null
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Please wait...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Username").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Password").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Forgot Password?").assertIsNotEnabled()
    }

    @Test
    fun loginScreen_displaysErrorMessage() {
        // Given
        val errorMessage = "Invalid username or password"
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onForgotPasswordClick = { },
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysSystemInformation() {
        // Given
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onForgotPasswordClick = { },
                    isLoading = false,
                    errorMessage = null
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("System Information").assertIsDisplayed()
        composeTestRule.onNodeWithText("1.0.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Production").assertIsDisplayed()
        composeTestRule.onNodeWithText("Samsung Galaxy Tab A9+").assertIsDisplayed()
        composeTestRule.onNodeWithText("Landscape").assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysDemoCredentials() {
        // Given
        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onForgotPasswordClick = { },
                    isLoading = false,
                    errorMessage = null
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Demo Credentials").assertIsDisplayed()
        composeTestRule.onNodeWithText("admin / admin123").assertIsDisplayed()
        composeTestRule.onNodeWithText("supervisor / super123").assertIsDisplayed()
        composeTestRule.onNodeWithText("operator / oper123").assertIsDisplayed()
    }

    @Test
    fun loginScreen_callsOnLoginClickWhenSignInPressed() {
        // Given
        var loginCalled = false
        var capturedUsername = ""
        var capturedPassword = ""

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                LoginScreen(
                    onLoginClick = { username, password ->
                        loginCalled = true
                        capturedUsername = username
                        capturedPassword = password
                    },
                    onForgotPasswordClick = { },
                    isLoading = false,
                    errorMessage = null
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Username").performTextInput("testuser")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Sign In").performClick()

        // Then
        assert(loginCalled)
        assert(capturedUsername == "testuser")
        assert(capturedPassword == "password123")
    }

    @Test
    fun loginScreen_callsOnForgotPasswordClickWhenButtonPressed() {
        // Given
        var forgotPasswordCalled = false

        composeTestRule.setContent {
            HitPromoWorkstationTheme {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onForgotPasswordClick = { forgotPasswordCalled = true },
                    isLoading = false,
                    errorMessage = null
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Forgot Password?").performClick()

        // Then
        assert(forgotPasswordCalled)
    }
}