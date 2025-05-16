package com.example.tripguru.data.repository

import com.example.tripguru.data.local.dao.TripDao
import com.example.tripguru.data.local.entity.toDomain
import com.example.tripguru.data.local.entity.toEntity
import com.example.tripguru.data.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao
) : TripRepository {
    override fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips().map { trips -> trips.map { it.toDomain() } }
    }

    override suspend fun getTripById(id: Long): Trip? {
        return tripDao.getTripById(id)?.toDomain()
    }

    override suspend fun insertTrip(trip: Trip): Long {
        return tripDao.insertTrip(trip.toEntity())
    }

    override suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip.toEntity())
    }

    override suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip.toEntity())
    }
}