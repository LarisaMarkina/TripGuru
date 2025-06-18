package com.example.tripguru.di

import android.content.Context
import androidx.room.Room
import com.example.tripguru.data.local.TripDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing database-related dependencies.
 *
 * This object provides instances of the [TripDatabase] and its associated DAOs
 * as singletons within the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): TripDatabase {
        return Room.databaseBuilder(
            appContext,
            TripDatabase::class.java,
            "trip_guru_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideTripDao(db: TripDatabase) = db.tripDao()
}