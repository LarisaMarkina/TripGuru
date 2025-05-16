package com.example.tripguru.presentation.trip.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripguru.R
import com.example.tripguru.data.model.Trip


/**
 * Represents a trip with its details.
 *
 * @property id The unique identifier of the trip.
 * @property name The name of the trip.
 * @property destination The destination of the trip.
 */
data class Trip(val id: Long, val name: String, val destination: String)

/**
 * Composable function that displays a single trip item.
 *
 * This function takes a [Trip] object and an onClick lambda function as input.
 * It displays the trip's name, destination, dates, and description (if available) in a Card.
 * The Card is clickable and invokes the onClick lambda with the trip's ID when clicked.
 *
 * @param trip The [Trip] object to display.
 * @param onClick A lambda function to be invoked when the trip item is clicked. It receives the trip's ID.
 * @param modifier The modifier to be applied to the trip item.
 */
@Composable
fun TripItem(
    trip: Trip,
    onClick: (tripId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick(trip.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(trip.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = stringResource(R.string.trip_destination, trip.destination))
            Text(stringResource(R.string.trip_dates, trip.startDate, trip.endDate))
            if (trip.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(trip.description, fontStyle = FontStyle.Italic)
            }
        }
    }
}