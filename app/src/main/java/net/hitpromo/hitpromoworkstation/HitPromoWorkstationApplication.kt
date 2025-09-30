package net.hitpromo.hitpromoworkstation

import android.app.Application
import android.content.res.Resources
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import dagger.hilt.android.HiltAndroidApp
import java.io.File

/**
 * Application class for HitPromo Workstation.
 *
 * This class serves as the entry point for Hilt dependency injection
 * and any application-level initialization for the industrial streaming workstation.
 */
@HiltAndroidApp
class HitPromoWorkstationApplication : Application() {

    companion object {
        private const val TAG = "HitPromoWorkstation"
        private const val AMPLIFY_CONFIG_FILE = "amplifyconfiguration"

        @Volatile
        private var isAmplifyInitialized = false

        @Volatile
        private var amplifyInitializationError: Throwable? = null

        /**
         * Check if Amplify is properly initialized.
         *
         * @return true if Amplify is initialized, false otherwise
         */
        fun isAmplifyConfigured(): Boolean = isAmplifyInitialized

        /**
         * Get the Amplify initialization error if any.
         *
         * @return The error that occurred during initialization, or null if successful
         */
        fun getAmplifyInitializationError(): Throwable? = amplifyInitializationError
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize AWS Amplify
        initializeAmplify()

        // Initialize any additional application-level components here
        // Such as crash reporting, analytics, etc.
    }

    /**
     * Initialize AWS Amplify with Cognito authentication plugin.
     *
     * This method validates configuration file existence before attempting
     * initialization and tracks initialization state for error handling.
     */
    private fun initializeAmplify() {
        try {
            // Validate configuration file exists
            if (!validateAmplifyConfiguration()) {
                val error = IllegalStateException("Amplify configuration file not found")
                amplifyInitializationError = error
                Log.e(TAG, "AWS Amplify configuration validation failed", error)
                return
            }

            // Check if already configured (prevents re-initialization)
            if (isAmplifyInitialized) {
                Log.d(TAG, "AWS Amplify is already initialized")
                return
            }

            // Add Cognito Auth plugin
            Amplify.addPlugin(AWSCognitoAuthPlugin())

            // Configure Amplify with configuration file
            Amplify.configure(applicationContext)

            isAmplifyInitialized = true
            amplifyInitializationError = null
            Log.i(TAG, "AWS Amplify initialized successfully")

        } catch (e: AmplifyException) {
            isAmplifyInitialized = false
            amplifyInitializationError = e
            Log.e(TAG, "Failed to initialize AWS Amplify", e)

            // Log specific details about the failure
            Log.e(TAG, "AmplifyException details: ${e.message}", e)
            e.cause?.let { cause ->
                Log.e(TAG, "Caused by: ${cause.message}", cause)
            }

        } catch (e: Exception) {
            isAmplifyInitialized = false
            amplifyInitializationError = e
            Log.e(TAG, "Unexpected error initializing AWS Amplify", e)
        }
    }

    /**
     * Validate that Amplify configuration file exists.
     *
     * Checks for amplifyconfiguration.json in the raw resources directory.
     *
     * @return true if configuration file exists, false otherwise
     */
    private fun validateAmplifyConfiguration(): Boolean {
        return try {
            // Try to get the resource ID for amplifyconfiguration
            val resourceId = resources.getIdentifier(
                AMPLIFY_CONFIG_FILE,
                "raw",
                packageName
            )

            if (resourceId == 0) {
                Log.e(TAG, "Amplify configuration file '$AMPLIFY_CONFIG_FILE.json' not found in res/raw")
                return false
            }

            // Try to open the resource to ensure it's readable
            val inputStream = resources.openRawResource(resourceId)
            inputStream.use {
                val available = it.available()
                if (available == 0) {
                    Log.e(TAG, "Amplify configuration file is empty")
                    return false
                }
                Log.d(TAG, "Amplify configuration file found and readable ($available bytes)")
            }

            true

        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Amplify configuration resource not found", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error validating Amplify configuration", e)
            false
        }
    }
}