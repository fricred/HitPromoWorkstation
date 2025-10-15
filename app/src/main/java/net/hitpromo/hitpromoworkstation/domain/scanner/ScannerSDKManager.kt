package net.hitpromo.hitpromoworkstation.domain.scanner

import com.zebra.scannercontrol.DCSScannerInfo
import com.zebra.scannercontrol.DCSSDKDefs

/**
 * Interface for managing the Zebra Scanner SDK.
 *
 * Provides high-level operations for scanner lifecycle management,
 * event subscription, and scanner queries. Implementations handle the
 * low-level SDK interactions and threading.
 */
interface ScannerSDKManager {
    /**
     * Set the delegate to receive scanner events.
     *
     * Only one delegate can be active at a time. Setting a new delegate
     * will replace the previous one. Setting null will remove the delegate.
     *
     * @param delegate The delegate to receive events, or null to remove
     */
    fun setDelegate(delegate: ScannerEventDelegate?)

    /**
     * Establish a communication session with a scanner.
     *
     * @param scannerId The unique identifier of the scanner to connect to
     * @return Result code indicating success or failure
     */
    fun connect(scannerId: Int): DCSSDKDefs.DCSSDK_RESULT

    /**
     * Terminate the communication session with a scanner.
     *
     * @param scannerId The unique identifier of the scanner to disconnect
     */
    fun disconnect(scannerId: Int)

    /**
     * Get list of available (discovered but not connected) scanners.
     *
     * @return List of available scanner information
     */
    fun getAvailableScanners(): List<DCSScannerInfo>

    /**
     * Get list of active (connected) scanners.
     *
     * @return List of active scanner information
     */
    fun getActiveScanners(): List<DCSScannerInfo>

    /**
     * Check if the SDK is properly initialized.
     *
     * @return true if SDK is initialized and ready, false otherwise
     */
    fun isInitialized(): Boolean
}
