package com.example.tripguru.domain.usecase.trip

import com.example.tripguru.data.model.Trip
import com.example.tripguru.data.repository.TripRepository

class DeleteTripUseCase(
    private val repository: TripRepository
) {
    suspend operator fun invoke(trip: Trip) {
        repository.deleteTrip(trip)
    }
}