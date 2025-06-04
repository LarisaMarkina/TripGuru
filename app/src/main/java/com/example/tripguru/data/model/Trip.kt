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
 * @property createDate
 * @constructor Create empty Trip
 */
data class Trip(
    val id: Long = 0L,
    val name: String,
    val destination: String?,
    val startDate: Long?,
    val endDate: Long?,
    val description: String?,
    val createDate: Long = System.currentTimeMillis()
)