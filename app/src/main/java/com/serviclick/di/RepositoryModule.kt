package com.serviclick.di

import com.serviclick.data.repository.AppointmentRepositoryImpl
import com.serviclick.data.repository.AuthRepositoryImpl
import com.serviclick.data.repository.UserRepositoryImpl
import com.serviclick.domain.repository.AppointmentRepository
import com.serviclick.domain.repository.AuthRepository
import com.serviclick.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    // NUEVO: Proveedor del repositorio de Citas
    @Provides
    @Singleton
    fun provideAppointmentRepository(): AppointmentRepository {
        return AppointmentRepositoryImpl()
    }
}