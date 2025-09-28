package net.hitpromo.hitpromoworkstation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.hitpromo.hitpromoworkstation.data.remote.CognitoAuthDataSource
import javax.inject.Singleton

/**
 * Hilt module for network and remote data source dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides CognitoAuthDataSource instance.
     */
    @Provides
    @Singleton
    fun provideCognitoAuthDataSource(): CognitoAuthDataSource {
        return CognitoAuthDataSource()
    }

    // TODO: Add AWS SDK configuration and other network dependencies
    // when implementing full AWS Cognito integration
}