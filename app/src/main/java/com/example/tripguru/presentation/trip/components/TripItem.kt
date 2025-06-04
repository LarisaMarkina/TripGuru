package com.example.tripguru.presentation.trip.components

import android.text.TextUtils.isEmpty
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Trip(val id: Long, val name: String, val destination: String)

@Composable
fun TripItem(
    trip: Trip,
    onClick: (tripId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick(trip.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Nazwa podróży
            Text(trip.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))

            // Cel podróży
            if (!isEmpty(trip.destination)) {
                trip.destination?.let { Text(text = stringResource(R.string.trip_destination, it)) }
            }

            // Daty podróży
            if (trip.endDate != null && trip.startDate != null) {
                val dateStartDisplay = dateFormatter.format(Date(trip.startDate))
                val dateEndDisplay = dateFormatter.format(Date(trip.endDate))
                Text(stringResource(R.string.trip_dates, dateStartDisplay, dateEndDisplay))
            }

            // Opis podróży
            if (!isEmpty(trip.description)) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(trip.description.toString(), fontStyle = FontStyle.Italic)
            }
        }
    }
}