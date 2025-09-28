package net.hitpromo.hitpromoworkstation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for HitPromo Workstation.
 *
 * This class serves as the entry point for Hilt dependency injection
 * and any application-level initialization for the industrial streaming workstation.
 */
@HiltAndroidApp
class HitPromoWorkstationApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize any application-level components here
        // Such as crash reporting, analytics, etc.
    }
}