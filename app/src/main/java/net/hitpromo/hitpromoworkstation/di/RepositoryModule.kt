package net.hitpromo.hitpromoworkstation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.hitpromo.hitpromoworkstation.data.repository.AuthRepositoryImpl
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import javax.inject.Singleton

/**
 * Hilt module for repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds AuthRepositoryImpl to AuthRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}