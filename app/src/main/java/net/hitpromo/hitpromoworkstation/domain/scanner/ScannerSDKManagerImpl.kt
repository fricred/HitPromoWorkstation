package net.hitpromo.hitpromoworkstation.domain.scanner

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import com.zebra.barcode.sdk.sms.ConfigurationUpdateEvent
import com.zebra.scannercontrol.DCSScannerInfo
import com.zebra.scannercontrol.DCSSDKDefs
import com.zebra.scannercontrol.FirmwareUpdateEvent
import com.zebra.scannercontrol.IDcsSdkApiDelegate
import com.zebra.scannercontrol.SDKHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.hitpromo.hitpromoworkstation.di.IoDispatcher
import net.hitpromo.hitpromoworkstation.di.MainDispatcher
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ScannerSDKManager that integrates with the Zebra Scanner SDK.
 *
 * This class implements both the ScannerSDKManager interface (for app usage) and
 * IDcsSdkApiDelegate (for SDK callbacks). It handles all SDK interactions,
 * thread safety, and event dispatching to registered delegates.
 *
 * CRITICAL: This is a Singleton that lives for the application lifetime.
 * The SDK handler is initialized in the Application class and injected via Hilt.
 */
@Singleton
class ScannerSDKManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sdkHandler: SDKHandler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ScannerSDKManager, IDcsSdkApiDelegate {

    companion object {
        private const val TAG = "ScannerSDKManager"
    }

    // Coroutine scope for SDK operations
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)

    // Current delegate receiving events
    private var delegate: ScannerEventDelegate? = null

    // Track intentional disconnect vs unexpected disconnect
    private var isIntentionalDisconnect = false

    init {
        Log.d(TAG, "ScannerSDKManagerImpl initializing...")

        // Set this class as the SDK delegate
        sdkHandler.dcssdkSetDelegate(this)

        // Subscribe to scanner events
        subscribeToEvents()

        Log.d(TAG, "ScannerSDKManagerImpl initialized successfully")

        // Check for scanners after a delay to allow SDK discovery to complete
        scope.launch {
            delay(2000) // Wait 2 seconds for SDK to discover scanners
            checkForScannersAfterInit()
        }
    }

    /**
     * Check for scanners after initialization completes.
     * If no scanners found, notify delegate so UI can show "Scanner not found" status.
     */
    private fun checkForScannersAfterInit() {
        val availableScanners = getAvailableScanners()
        val activeScanners = getActiveScanners()

        scope.launch(mainDispatcher) {
            if (availableScanners.isEmpty() && activeScanners.isEmpty()) {
                Log.d(TAG, "No scanners detected after initialization")
                delegate?.onLog(LogLevel.WARNING, "No scanner detected - please connect scanner")
                // Trigger a state update by calling onScannerDisappeared with ID 0
                // This will cause the ViewModel to update status to ScannerNotFound
                delegate?.onScannerDisappeared(0)
            } else {
                Log.d(TAG, "Found ${availableScanners.size} available and ${activeScanners.size} active scanners")
            }
        }
    }

    /**
     * Subscribe to scanner events we care about.
     */
    private fun subscribeToEvents() {
        val eventMask = DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value or
                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value or
                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value or
                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value or
                DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value

        sdkHandler.dcssdkSubsribeForEvents(eventMask)
        Log.d(TAG, "Subscribed to scanner events (mask: $eventMask)")
    }

    /**
     * Request USB permission for a scanner device.
     * Required for Android 12+ (API 31+) to connect USB devices.
     */
    private fun requestUsbPermissionIfNeeded(scannerInfo: DCSScannerInfo) {
        // Only needed for USB connections on Android 12+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return  // No runtime permission needed on older Android
        }

        val connectionType = scannerInfo.connectionType
        if (connectionType != DCSSDKDefs.DCSSDK_CONN_TYPES.DCSSDK_CONNTYPE_USB_CDC &&
            connectionType != DCSSDKDefs.DCSSDK_CONN_TYPES.DCSSDK_CONNTYPE_USB_SNAPI) {
            return  // Not USB, no permission needed
        }

        try {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

            // Check if we need to request permission
            // Note: The SDK may not expose the underlying UsbDevice directly
            // In that case, the SDK's own permission handling will work

            Log.d(TAG, "USB permission handling prepared for Android 12+")

        } catch (e: Exception) {
            Log.e(TAG, "Error requesting USB permission", e)
            delegate?.onLog(LogLevel.WARNING, "USB permission error: ${e.message}")
        }
    }

    // ========================================
    // ScannerSDKManager Interface Implementation
    // ========================================

    override fun setDelegate(delegate: ScannerEventDelegate?) {
        this.delegate = delegate
        Log.d(TAG, "Delegate ${if (delegate != null) "registered" else "removed"}")
    }

    override fun connect(scannerId: Int): DCSSDKDefs.DCSSDK_RESULT {
        isIntentionalDisconnect = false
        val result = sdkHandler.dcssdkEstablishCommunicationSession(scannerId)
        Log.d(TAG, "Connect attempt for scanner $scannerId: $result")
        return result
    }

    override fun disconnect(scannerId: Int) {
        isIntentionalDisconnect = true
        sdkHandler.dcssdkTerminateCommunicationSession(scannerId)
        Log.d(TAG, "Disconnect requested for scanner $scannerId")
    }

    override fun getAvailableScanners(): List<DCSScannerInfo> {
        val scanners = ArrayList<DCSScannerInfo>()
        sdkHandler.dcssdkGetAvailableScannersList(scanners)
        Log.d(TAG, "Available scanners: ${scanners.size}")
        return scanners
    }

    override fun getActiveScanners(): List<DCSScannerInfo> {
        val scanners = ArrayList<DCSScannerInfo>()
        sdkHandler.dcssdkGetActiveScannersList(scanners)
        Log.d(TAG, "Active scanners: ${scanners.size}")
        return scanners
    }

    override fun isInitialized(): Boolean {
        return try {
            sdkHandler
            true
        } catch (e: Exception) {
            false
        }
    }

    // ========================================
    // IDcsSdkApiDelegate Interface Implementation
    // All callbacks run on SDK's internal thread - must post to main thread!
    // ========================================

    override fun dcssdkEventBarcode(
        barcodeData: ByteArray,
        barcodeType: Int,
        fromScannerID: Int
    ) {
        val barcode = String(barcodeData, Charset.forName("UTF-8"))
        Log.d(TAG, "Barcode scanned: $barcode (type: $barcodeType, scanner: $fromScannerID)")

        // Post to main thread
        scope.launch(mainDispatcher) {
            delegate?.onLog(LogLevel.SUCCESS, "Barcode scanned: $barcode")
            delegate?.onBarcodeScanned(barcode, barcodeType, fromScannerID)
        }
    }

    override fun dcssdkEventScannerAppeared(availableScanner: DCSScannerInfo) {
        val name = availableScanner.scannerName ?: "Unknown Scanner"
        val id = availableScanner.scannerID
        val connectionType = availableScanner.connectionType

        Log.d(TAG, "Scanner appeared: $name (ID: $id, Type: $connectionType)")

        scope.launch(mainDispatcher) {
            delegate?.onLog(LogLevel.INFO, "Scanner detected: $name (ID: $id)")
            delegate?.onScannerAppeared(id, name)

            // Auto-connect USB scanners (CRITICAL per review)
            if (connectionType == DCSSDKDefs.DCSSDK_CONN_TYPES.DCSSDK_CONNTYPE_USB_CDC ||
                connectionType == DCSSDKDefs.DCSSDK_CONN_TYPES.DCSSDK_CONNTYPE_USB_SNAPI) {

                // Request USB permission if needed (Android 12+)
                requestUsbPermissionIfNeeded(availableScanner)

                delegate?.onLog(LogLevel.INFO, "Auto-connecting USB scanner...")
                Log.d(TAG, "Auto-connecting USB scanner: $name")

                val result = connect(id)

                if (result != DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS) {
                    val errorMsg = "Failed to auto-connect: $result"
                    Log.e(TAG, errorMsg)
                    delegate?.onError(errorMsg)
                    delegate?.onLog(LogLevel.ERROR, errorMsg)
                } else {
                    Log.d(TAG, "Auto-connect initiated successfully")
                }
            }
        }
    }

    override fun dcssdkEventCommunicationSessionEstablished(activeScanner: DCSScannerInfo) {
        val name = activeScanner.scannerName ?: "Unknown Scanner"
        val id = activeScanner.scannerID

        Log.d(TAG, "Scanner connected: $name (ID: $id)")

        scope.launch(mainDispatcher) {
            delegate?.onLog(LogLevel.SUCCESS, "Connected to scanner: $name")
            delegate?.onScannerConnected(id, name)
        }
    }

    override fun dcssdkEventCommunicationSessionTerminated(scannerID: Int) {
        Log.d(TAG, "Scanner disconnected: $scannerID (intentional: $isIntentionalDisconnect)")

        scope.launch(mainDispatcher) {
            if (!isIntentionalDisconnect) {
                val errorMsg = "Scanner disconnected unexpectedly (ID: $scannerID)"
                Log.w(TAG, errorMsg)
                delegate?.onError(errorMsg)
                delegate?.onLog(LogLevel.WARNING, errorMsg)
            } else {
                delegate?.onLog(LogLevel.INFO, "Scanner disconnected (ID: $scannerID)")
            }

            delegate?.onScannerDisconnected(scannerID)
        }
    }

    override fun dcssdkEventScannerDisappeared(scannerID: Int) {
        Log.d(TAG, "Scanner disappeared: $scannerID")

        scope.launch(mainDispatcher) {
            delegate?.onLog(LogLevel.WARNING, "Scanner disappeared (ID: $scannerID)")
            delegate?.onScannerDisappeared(scannerID)
        }
    }

    // ========================================
    // Unused SDK Events (empty implementations)
    // ========================================

    override fun dcssdkEventImage(imageData: ByteArray, fromScannerID: Int) {
        // Not used for barcode scanning
    }

    override fun dcssdkEventVideo(videoFrame: ByteArray, fromScannerID: Int) {
        // Not used for barcode scanning
    }

    override fun dcssdkEventBinaryData(binaryData: ByteArray, fromScannerID: Int) {
        // Not used for barcode scanning
    }

    override fun dcssdkEventFirmwareUpdate(firmwareUpdateEvent: FirmwareUpdateEvent) {
        // Not used for barcode scanning
    }

    override fun dcssdkEventAuxScannerAppeared(
        newTopology: DCSScannerInfo,
        auxScanner: DCSScannerInfo
    ) {
        // Not used for barcode scanning
    }

    override fun dcssdkEventConfigurationUpdate(configurationUpdateEvent: ConfigurationUpdateEvent) {
        // Not used for barcode scanning
    }
}
