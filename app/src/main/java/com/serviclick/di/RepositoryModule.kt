package com.serviclick.di

import com.serviclick.data.repository.AppointmentRepositoryImpl
import com.serviclick.data.repository.AuthRepositoryImpl
import com.serviclick.data.repository.ChatRepositoryImpl
import com.serviclick.data.repository.UserRepositoryImpl
import com.serviclick.domain.repository.AppointmentRepository
import com.serviclick.domain.repository.AuthRepository
import com.serviclick.domain.repository.ChatRepository
import com.serviclick.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Inyección de Dependencias (DI) configurado para Dagger Hilt.
 * Provee las instancias concretas de los repositorios a lo largo de toda la app.
 *
 * Anotado con `@Module` e instalado en el `SingletonComponent`, lo que significa que
 * estas instancias vivirán durante todo el ciclo de vida de la aplicación.
 */
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

    // Proveedor del repositorio de Citas
    @Provides
    @Singleton
    fun provideAppointmentRepository(): AppointmentRepository {
        return AppointmentRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository {
        return chatRepositoryImpl
    }
}