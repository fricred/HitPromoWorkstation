package net.hitpromo.hitpromoworkstation.di

import android.content.Context
import com.zebra.scannercontrol.SDKHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import net.hitpromo.hitpromoworkstation.HitPromoWorkstationApplication
import net.hitpromo.hitpromoworkstation.domain.scanner.ScannerSDKManager
import net.hitpromo.hitpromoworkstation.domain.scanner.ScannerSDKManagerImpl
import javax.inject.Singleton

/**
 * Hilt module for Zebra Scanner SDK dependencies.
 *
 * Provides the ScannerSDKManager as a singleton, ensuring the SDK
 * is initialized once and lives for the application lifetime.
 */
@Module
@InstallIn(SingletonComponent::class)
object ScannerModule {

    /**
     * Provides SDKHandler singleton.
     *
     * The SDK handler is initialized in HitPromoWorkstationApplication.onCreate()
     * and provided here for dependency injection.
     */
    @Provides
    @Singleton
    fun provideSDKHandler(
        @ApplicationContext context: Context
    ): SDKHandler {
        val app = context.applicationContext as HitPromoWorkstationApplication
        return app.sdkHandler
    }

    /**
     * Provides ScannerSDKManager singleton.
     *
     * The SDK handler is initialized in HitPromoWorkstationApplication.onCreate()
     * and this manager wraps it for use throughout the app.
     */
    @Provides
    @Singleton
    fun provideScannerSDKManager(
        @ApplicationContext context: Context,
        sdkHandler: SDKHandler,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @MainDispatcher mainDispatcher: CoroutineDispatcher
    ): ScannerSDKManager {
        return ScannerSDKManagerImpl(context, sdkHandler, ioDispatcher, mainDispatcher)
    }
}
