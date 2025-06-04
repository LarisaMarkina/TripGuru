package com.example.tripguru.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tripguru.data.model.Trip

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val destination: String?,
    val startDate: Long?,
    val endDate: Long?,
    val description: String?,
    val createDate: Long = System.currentTimeMillis()
)

fun TripEntity.toDomain(): Trip = Trip(
    id = id,
    name = name,
    destination = destination,
    startDate = startDate,
    endDate = endDate,
    description = description
)

fun Trip.toEntity(): TripEntity = TripEntity(
    id = id,
    name = name,
    destination = destination,
    startDate = startDate,
    endDate = endDate,
    description = description
)

