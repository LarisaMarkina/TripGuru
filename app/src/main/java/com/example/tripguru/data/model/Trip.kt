package com.example.tripguru.data.model

/**
 * Trip - podróż droga stół
 *
 * @property id - id o
 * @property name
 * @property destination
 * @property startDate
 * @property endDate
 * @property description
 * @constructor Create empty Trip
 */
data class Trip(
    val id: Long = 0L,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val description: String
)