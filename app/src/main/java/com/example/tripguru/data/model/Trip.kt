package com.example.tripguru.data.model

/**
 * Trip - podróż droga stół
 *
 * @property id - id podróży
 * @property name - nazwa
 * @property destination - cel
 * @property participantsNumber - liczba uczestników
 * @property startDate - data rozpoczęcia
 * @property endDate - data zakończenia
 * @property description - opis
 * @property createDate - data utworzenia
 * @constructor Create empty Trip
 */
data class Trip(
    val id: Long = 0L,
    val name: String,
    val destination: String?,
    val participantsNumber: Int? = 1,
    val startDate: Long?,
    val endDate: Long?,
    val description: String?,
    val createDate: Long = System.currentTimeMillis()
)