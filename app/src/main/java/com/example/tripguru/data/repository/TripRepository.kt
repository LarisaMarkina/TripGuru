package com.example.tripguru.data.repository

import com.example.tripguru.data.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getAllTrips(): Flow<List<Trip>>
    fun getTripById(id: Long): Flow<Trip?>
    suspend fun insertTrip(trip: Trip): Long
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(trip: Trip)
}