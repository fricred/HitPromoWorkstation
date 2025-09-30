package net.hitpromo.hitpromoworkstation.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.hitpromo.hitpromoworkstation.data.remote.CognitoAuthDataSource
import net.hitpromo.hitpromoworkstation.util.AndroidStringProvider
import net.hitpromo.hitpromoworkstation.util.NetworkMonitor
import net.hitpromo.hitpromoworkstation.util.StringProvider
import javax.inject.Singleton

/**
 * Hilt module for network and remote data source dependencies.
 *
 * AWS Amplify is initialized in HitPromoWorkstationApplication.onCreate()
 * and is available globally via the Amplify singleton.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindStringProvider(
        androidStringProvider: AndroidStringProvider
    ): StringProvider

    companion object {
        /**
         * Provides CognitoAuthDataSource instance.
         *
         * The data source uses AWS Amplify Auth APIs which are configured
         * via amplifyconfiguration.json in the app's res/raw directory.
         */
        @Provides
        @Singleton
        fun provideCognitoAuthDataSource(
            networkMonitor: NetworkMonitor,
            stringProvider: StringProvider
        ): CognitoAuthDataSource {
            return CognitoAuthDataSource(networkMonitor, stringProvider)
        }

        /**
         * Provides NetworkMonitor instance.
         */
        @Provides
        @Singleton
        fun provideNetworkMonitor(
            @ApplicationContext context: Context
        ): NetworkMonitor {
            return NetworkMonitor(context)
        }
    }
}