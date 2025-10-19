package net.hitpromo.hitpromoworkstation.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.hitpromo.hitpromoworkstation.BuildConfig
import net.hitpromo.hitpromoworkstation.data.local.UserPreferences
import net.hitpromo.hitpromoworkstation.data.remote.BadgeApiService
import net.hitpromo.hitpromoworkstation.data.remote.CognitoAuthDataSource
import net.hitpromo.hitpromoworkstation.data.remote.WorkersApiService
import net.hitpromo.hitpromoworkstation.util.AndroidStringProvider
import net.hitpromo.hitpromoworkstation.util.NetworkMonitor
import net.hitpromo.hitpromoworkstation.util.StringProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
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
        // Base URL is configured in app/build.gradle.kts
        // ALL API endpoints use this single base URL
        private val BASE_URL = BuildConfig.API_BASE_URL
        private const val TIMEOUT_SECONDS = 30L

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

        /**
         * Provides Moshi instance for JSON serialization.
         */
        @Provides
        @Singleton
        fun provideMoshi(): Moshi {
            return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }

        /**
         * Provides OkHttpClient with logging and token authentication interceptors.
         *
         * The token interceptor is added first (before logging) to inject the
         * Authorization header before the request is logged.
         */
        @Provides
        @Singleton
        fun provideOkHttpClient(
            userPreferences: UserPreferences
        ): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.BASIC
                }
            }

            val tokenInterceptor = TokenAuthInterceptor(userPreferences)

            android.util.Log.d(
                "NetworkModule",
                "Creating OkHttpClient with interceptors. Base URL: $BASE_URL"
            )

            return OkHttpClient.Builder()
                // Add token interceptor first so it runs before logging
                .addInterceptor(tokenInterceptor)
                // Add logging interceptor to see full request/response
                .addInterceptor(loggingInterceptor)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
        }

        /**
         * Provides Retrofit instance for all API endpoints.
         *
         * All API requests (badge, workers, etc) use this single Retrofit instance
         * which is configured with the OkHttpClient that includes TokenAuthInterceptor.
         */
        @Provides
        @Singleton
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            moshi: Moshi
        ): Retrofit {
            android.util.Log.d(
                "NetworkModule",
                "Creating Retrofit instance with base URL: $BASE_URL"
            )
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        /**
         * Provides BadgeApiService instance.
         */
        @Provides
        @Singleton
        fun provideBadgeApiService(retrofit: Retrofit): BadgeApiService {
            return retrofit.create(BadgeApiService::class.java)
        }

        /**
         * Provides WorkersApiService instance.
         *
         * Uses the same Retrofit instance as BadgeApiService, ensuring all requests
         * are authenticated via TokenAuthInterceptor.
         */
        @Provides
        @Singleton
        fun provideWorkersApiService(retrofit: Retrofit): WorkersApiService {
            return retrofit.create(WorkersApiService::class.java)
        }
    }
}