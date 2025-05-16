package com.example.tripguru.domain.usecase.trip

data class TripUseCases(
    val addTrip: AddTripUseCase,
    val getTrips: GetTripsUseCase,
    val getTripById: GetTripByIdUseCase,
    val updateTrip: UpdateTripUseCase,
    val deleteTrip: DeleteTripUseCase
)