package com.example.tripguru

object AppRoutes {
    const val TRIP_LIST_ROUTE = "trip_list"

    // dodawanie podróży
    const val ADD_TRIP_ROUTE = "add_trip"

    // szczegóły podróży
    const val TRIP_DETAILS_PREFIX = "trip_details"
    const val TRIP_DETAILS_ARG_ID = "tripId"
    const val TRIP_DETAILS_ROUTE = "$TRIP_DETAILS_PREFIX/{$TRIP_DETAILS_ARG_ID}"
    fun tripDetailsRoute(tripId: Long) = "$TRIP_DETAILS_PREFIX/$tripId"

    // edycja podróży
    const val EDIT_TRIP_PREFIX = "edit_trip"
    const val EDIT_TRIP_ARG_ID = "tripId"
    const val EDIT_TRIP_ROUTE = "$EDIT_TRIP_PREFIX/{$EDIT_TRIP_ARG_ID}"
    fun editTripRoute(tripId: Long): String = "$EDIT_TRIP_PREFIX/$tripId"
}