package net.hitpromo.hitpromoworkstation

import android.app.Application
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.zebra.scannercontrol.DCSSDKDefs
import com.zebra.scannercontrol.SDKHandler
import dagger.hilt.android.HiltAndroidApp
import java.io.File

/**
 * Application class for HitPromo Workstation.
 *
 * This class serves as the entry point for Hilt dependency injection
 * and any application-level initialization for the industrial streaming workstation.
 */
@HiltAndroidApp
class HitPromoWorkstationApplication : Application(), DefaultLifecycleObserver {

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

    /**
     * Zebra Scanner SDK handler - initialized once for app lifetime.
     * CRITICAL: This must be accessible to ScannerSDKManagerImpl via lazy delegate.
     */
    lateinit var sdkHandler: SDKHandler
        private set

    override fun onCreate() {
        super<Application>.onCreate()

        // Register lifecycle observer for SDK cleanup
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        // Initialize AWS Amplify
        initializeAmplify()

        // Initialize Zebra Scanner SDK
        initializeZebraScannerSDK()

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

    /**
     * Initialize Zebra Scanner SDK.
     *
     * Creates the SDK handler that will be used throughout the app lifetime
     * for communicating with barcode scanners. The actual delegate and event
     * subscription is handled by ScannerSDKManagerImpl.
     */
    private fun initializeZebraScannerSDK() {
        try {
            Log.d(TAG, "Initializing Zebra Scanner SDK...")

            // Create SDK handler
            // Parameters: context, enableUsbDiscovery, throwExceptions
            sdkHandler = SDKHandler(this, true, false)

            // Enable scanner detection
            sdkHandler.dcssdkEnableAvailableScannersDetection(true)

            // Set operational modes for USB scanner support
            sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_USB_CDC)
            sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI)

            Log.i(TAG, "Zebra Scanner SDK initialized successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Zebra Scanner SDK", e)
            // SDK initialization failure is not fatal - app can still function
            // without scanner, but badge scanning won't work
        }
    }

    /**
     * Called when app goes to background.
     * Release scanner resources to prevent leaks.
     */
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d(TAG, "App backgrounded - releasing scanner SDK")
        releaseZebraScannerSDK()
    }

    /**
     * Called when app comes to foreground.
     * Re-initialize scanner if needed.
     */
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d(TAG, "App foregrounded - checking scanner SDK")
        // SDK is already initialized, just log
    }

    /**
     * Release Zebra Scanner SDK resources.
     * Called when app is backgrounded or terminated.
     */
    private fun releaseZebraScannerSDK() {
        try {
            if (::sdkHandler.isInitialized) {
                sdkHandler.dcssdkClose()
                Log.d(TAG, "Zebra Scanner SDK released successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing Zebra Scanner SDK", e)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        releaseZebraScannerSDK()
    }
}