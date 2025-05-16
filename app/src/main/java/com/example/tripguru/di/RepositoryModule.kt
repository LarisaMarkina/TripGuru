package com.example.tripguru.di

import com.example.tripguru.data.local.dao.TripDao
import com.example.tripguru.data.repository.TripRepository
import com.example.tripguru.data.repository.TripRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides repository dependencies.
 *
 * This module is responsible for providing instances of repository implementations
 * like [TripRepository]. These repositories typically act as intermediaries between
 * data sources (like Room DAOs or network APIs) and the application's use cases or ViewModels.
 */
@Module
@InstallIn(SingletonComponent::class) // Or the appropriate component
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTripRepository(
        tripDao: TripDao
    ): TripRepository {
        return TripRepositoryImpl(tripDao)
    }
}