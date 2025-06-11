package com.example.tripguru

object AppRoutes {
    const val TRIP_LIST_ROUTE = "trip_list"
    const val ADD_TRIP_ROUTE = "add_trip"
    const val SHOW_TRIP_PREFIX = "show_trip"
    const val SHOW_TRIP_ARG_ID = "tripId"
    const val SHOW_TRIP_ROUTE = "$SHOW_TRIP_PREFIX/{$SHOW_TRIP_ARG_ID}"

    // Funkcja pomocnicza do tworzenia trasy z ID
    fun tripDetailsRoute(tripId: Long) = "$SHOW_TRIP_PREFIX/$tripId"
}