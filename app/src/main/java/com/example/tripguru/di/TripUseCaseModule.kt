package com.example.tripguru.di

import com.example.tripguru.data.repository.TripRepository
import com.example.tripguru.domain.usecase.trip.AddTripUseCase
import com.example.tripguru.domain.usecase.trip.DeleteTripUseCase
import com.example.tripguru.domain.usecase.trip.GetTripByIdUseCase
import com.example.tripguru.domain.usecase.trip.GetTripsUseCase
import com.example.tripguru.domain.usecase.trip.TripUseCases
import com.example.tripguru.domain.usecase.trip.UpdateTripUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Dagger Hilt module that provides the [TripUseCases] to the application.
 *
 * This module is installed in the [ViewModelComponent], meaning the dependencies provided
 * by this module will be available to ViewModels.
 */
@Module
@InstallIn(ViewModelComponent::class)
object TripUseCaseModule {

    @Provides
    fun provideTripUseCases(repository: TripRepository): TripUseCases {
        return TripUseCases(
            addTrip = AddTripUseCase(repository),
            getTrips = GetTripsUseCase(repository),
            getTripById = GetTripByIdUseCase(repository),
            updateTrip = UpdateTripUseCase(repository),
            deleteTrip = DeleteTripUseCase(repository)
        )
    }
}