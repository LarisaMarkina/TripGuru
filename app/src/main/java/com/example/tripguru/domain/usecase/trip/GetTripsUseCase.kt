package com.example.tripguru.domain.usecase.trip

import com.example.tripguru.data.model.Trip
import com.example.tripguru.data.repository.TripRepository
import kotlinx.coroutines.flow.Flow

class GetTripsUseCase(
    private val repository: TripRepository
) {
    operator fun invoke(): Flow<List<Trip>> {
        return repository.getAllTrips()
    }
}