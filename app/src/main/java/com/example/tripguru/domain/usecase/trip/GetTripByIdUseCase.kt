package com.example.tripguru.domain.usecase.trip

import com.example.tripguru.data.model.Trip
import com.example.tripguru.data.repository.TripRepository

class GetTripByIdUseCase(
    private val repository: TripRepository
) {
    suspend operator fun invoke(id: Long): Trip? {
        return repository.getTripById(id)
    }
}