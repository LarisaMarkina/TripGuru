package com.example.tripguru.presentation.trip

import android.text.TextUtils.isEmpty
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tripguru.R
import com.example.tripguru.data.model.Trip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    onTripClick: (Long) -> Unit,
    snackBarMessage: String?,
    onConsumeSnackBarMessage: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val tripList by viewModel.trips.collectAsState()

    // Ładowanie listy podróży
    LaunchedEffect(true) {
        viewModel.loadTrips()
    }

    // Obsługa komunikatów snackBar
    LaunchedEffect(snackBarMessage) {
        if (snackBarMessage != null) {
            snackBarHostState.showSnackbar(snackBarMessage)
            onConsumeSnackBarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                Text(
                    stringResource(R.string.title_my_trips),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        // Pływający przycisk do dodawania nowych podróży
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTripClick) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.button_label_add_trip_content)
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
                TripItem(
                    trip = trip,
                    onClick = {
                        onTripClick(trip.id)
                    }
                )
            }
        }
    }
}

@Composable
fun TripItem(
    trip: Trip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Nazwa podróży
            Text(trip.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))

            // Cel podróży
            if (!isEmpty(trip.destination)) {
                trip.destination?.let {
                    Text(
                        text = stringResource(
                            R.string.label_trip_destination,
                            it
                        )
                    )
                }
            }

            // Daty podróży
            if (trip.endDate != null && trip.startDate != null) {
                val dateStartDisplay = dateFormatter.format(Date(trip.startDate))
                val dateEndDisplay = dateFormatter.format(Date(trip.endDate))
                Text(stringResource(R.string.label_trip_dates, dateStartDisplay, dateEndDisplay))
            }
        }
    }
}
