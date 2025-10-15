package net.hitpromo.hitpromoworkstation.domain.scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log

/**
 * BroadcastReceiver for handling USB permission requests.
 * Required for Android 12+ USB device connections.
 */
class UsbPermissionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_USB_PERMISSION = "net.hitpromo.hitpromoworkstation.USB_PERMISSION"
        private const val TAG = "UsbPermissionReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_USB_PERMISSION) {
            synchronized(this) {
                val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    device?.let {
                        Log.d(TAG, "USB permission granted for device: ${it.deviceName}")
                        // Permission granted - connection will proceed automatically
                        // The SDK's auto-connection logic will handle the actual connection
                    }
                } else {
                    Log.w(TAG, "USB permission denied for device: ${device?.deviceName}")
                    // TODO: Notify user that scanner permission was denied
                }
            }
        }
    }
}
