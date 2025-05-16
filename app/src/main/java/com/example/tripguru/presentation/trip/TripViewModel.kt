package com.example.tripguru.presentation.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripguru.data.model.Trip
import com.example.tripguru.domain.usecase.trip.TripUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing trip data and interacting with trip use cases.
 *
 * This ViewModel provides access to a list of trips and functions to perform CRUD operations
 * (Create, Read, Update, Delete) on trips. It uses [HiltViewModel] for dependency injection
 * and [viewModelScope] for coroutine management.
 *
 * @property tripUseCases The use cases responsible for handling trip-related business logic.
 */
@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripUseCases: TripUseCases
) : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    fun loadTrips() {
        viewModelScope.launch {
            tripUseCases.getTrips().collect {
                _trips.value = it
            }
        }
    }

    fun insertTrip(trip: Trip) {
        viewModelScope.launch {
            tripUseCases.addTrip(trip)
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            tripUseCases.deleteTrip(trip)
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            tripUseCases.updateTrip(trip)
        }
    }

    suspend fun getTripById(id: Long): Trip? {
        return tripUseCases.getTripById(id)
    }
}