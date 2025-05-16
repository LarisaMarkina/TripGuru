package com.example.tripguru.presentation.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tripguru.R
import com.example.tripguru.presentation.trip.components.TripItem

/**
 * Trip list screen.
 *
 * Displays a list of trips and provides options to add new trips or view details of existing ones.
 *
 * @param viewModel The [TripViewModel] responsible for managing trip data. Defaults to a Hilt-provided instance.
 * @param onAddTripClick Lambda function invoked when the add trip button is clicked.
 * @param onTripClick Lambda function invoked when a trip item is clicked, providing the ID of the clicked trip.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(
    viewModel: TripViewModel = hiltViewModel(),
    onAddTripClick: () -> Unit,
    onTripClick: (Long) -> Unit
) {
    val tripList by viewModel.trips.collectAsState()

    // Przy uruchomieniu ekranu, załaduj listę podróży
    LaunchedEffect(true) {
        viewModel.loadTrips()
    }

    Scaffold(
        // Pasek górny z tytułem "Moje podróże"
        topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(R.string.my_trips_title),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            })
        },
        // Pływający przycisk do dodawania nowych podróży
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTripClick) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_trip_content_button_label)
                )
            }
        }
    ) { paddingValues ->
        // Lista elementów pionowa, przewijana
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Elementy listy (podróże)
            items(
                items = tripList,
                key = { trip -> trip.id }
            ) { trip ->
                TripItem(trip = trip, onClick = { onTripClick(trip.id) })
            }
        }
    }
}
