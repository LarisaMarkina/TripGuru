package com.example.tripguru.presentation.trip

import android.text.TextUtils.isEmpty
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tripguru.R
import com.example.tripguru.data.model.Trip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TripDetailsScreen(
    tripId: Long,
    viewModel: TripViewModel = hiltViewModel(),
    onNavigateBackWithResult: (String) -> Unit,
    onCancel: () -> Unit
) {
    val trip by viewModel.selectedTripDetails.collectAsState()
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = tripId) {
        viewModel.loadTripDetails(tripId)
    }

    // Główny ekran
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {

            // Nagłówek ekranu
            TopAppBar(
                // Nazwa podróży
                title = {
                    Text(
                        trip?.name ?: stringResource(R.string.title_trip_details),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onCancel()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.button_label_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_desc_menu)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.button_label_delete)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showMenu = false
                                showDialog = true
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val currentTrip = trip) {
                null -> {
                    CircularProgressIndicator()
                }

                else -> {
                    TripDetailsContent(trip = currentTrip)
                }
            }
        }
    }

    // Dialog potwierdzający usunięcie podróży
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.dialog_title_confirm_delete)) },
            text = { Text(stringResource(R.string.dialog_text_confirm_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    trip?.let { viewModel.deleteTrip(it) }
                    showDialog = false
                    onNavigateBackWithResult(context.getString(R.string.msg_success_trip_deleted))
                }) {
                    Text(stringResource(R.string.button_label_delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.button_label_back))
                }
            }
        )
    }
}

@Composable
fun TripDetailsContent(trip: Trip) {

    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

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

        // Opis podróży
        if (!isEmpty(trip.description)) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(trip.description.toString(), fontStyle = FontStyle.Italic)
        }
    }
}


