package net.hitpromo.hitpromoworkstation.domain.scanner

/**
 * Delegate interface for receiving scanner events.
 *
 * Implementations of this interface will receive callbacks from the Zebra Scanner SDK
 * when scanner-related events occur (appearance, connection, barcode scans, errors, etc.).
 */
interface ScannerEventDelegate {
    /**
     * Called when a scanner device is detected and appears in the available scanners list.
     *
     * @param scannerId Unique identifier for the scanner
     * @param name Human-readable name of the scanner
     */
    fun onScannerAppeared(scannerId: Int, name: String)

    /**
     * Called when a communication session is successfully established with a scanner.
     *
     * @param scannerId Unique identifier for the scanner
     * @param name Human-readable name of the scanner
     */
    fun onScannerConnected(scannerId: Int, name: String)

    /**
     * Called when the communication session with a scanner is terminated.
     *
     * @param scannerId Unique identifier for the scanner
     */
    fun onScannerDisconnected(scannerId: Int)

    /**
     * Called when a scanner device disappears (unplugged, powered off, out of range, etc.).
     *
     * @param scannerId Unique identifier for the scanner
     */
    fun onScannerDisappeared(scannerId: Int)

    /**
     * Called when a barcode is successfully scanned.
     *
     * @param barcode The decoded barcode data as a string
     * @param type The barcode type (symbology)
     * @param scannerId Unique identifier for the scanner that performed the scan
     */
    fun onBarcodeScanned(barcode: String, type: Int, scannerId: Int)

    /**
     * Called when an error occurs in the scanner SDK.
     *
     * @param message Human-readable error message
     */
    fun onError(message: String)

    /**
     * Called to log diagnostic information for debugging.
     *
     * @param level The log level (INFO, SUCCESS, WARNING, ERROR)
     * @param message The log message
     */
    fun onLog(level: LogLevel, message: String)
}

/**
 * Log severity levels for debug logging.
 */
enum class LogLevel {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}
