package com.example.tripguru.domain.usecase.trip

import com.example.tripguru.data.model.Trip
import com.example.tripguru.data.repository.TripRepository
import kotlinx.coroutines.flow.Flow

class GetTripByIdUseCase(
    private val repository: TripRepository
) {
    operator fun invoke(id: Long): Flow<Trip?> {
        return repository.getTripById(id)
    }
}