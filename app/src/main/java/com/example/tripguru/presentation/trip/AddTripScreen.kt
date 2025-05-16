package com.example.tripguru.presentation.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tripguru.R
import com.example.tripguru.data.model.Trip

data class AddTripFormState(
    var name: String = "",
    var destination: String = "",
    var description: String = "",
    var startDate: String = "",
    var endDate: String = ""
)

@Composable
fun AddTripScreen(
    viewModel: TripViewModel = hiltViewModel(),
    onTripSaved: () -> Unit,
    onCancel: () -> Unit
) {

    val formState by remember { mutableStateOf(AddTripFormState()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tytuł ekranu
        Text(
            stringResource(R.string.add_trip_title),
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 25.dp),
            fontWeight = FontWeight.Bold
        )

        // Nazwa
        OutlinedTextField(
            value = formState.name,
            onValueChange = { formState.name = it },
            label = { Text(stringResource(R.string.tripe_name_field)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Cel
        OutlinedTextField(
            value = formState.destination,
            onValueChange = { formState.destination = it },
            label = { Text(stringResource(R.string.trip_destination_field)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Data rozpoczęcia
        OutlinedTextField(
            value = formState.startDate,
            onValueChange = { formState.startDate = it },
            label = { Text(stringResource(R.string.trip_date_start_field)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Data zakończenia
        OutlinedTextField(
            value = formState.endDate,
            onValueChange = { formState.endDate = it },
            label = { Text(stringResource(R.string.trip_date_end_field)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Opis
        OutlinedTextField(
            value = formState.description,
            onValueChange = { formState.description = it },
            label = { Text(stringResource(R.string.trip_description_field)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Pasek z przyciskami
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Anuluj
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.back_button_label))
            }

            // Zapisz
            Button(
                onClick = {
                    val trip = Trip(
                        name = formState.name,
                        destination = formState.destination,
                        description = formState.description,
                        startDate = formState.startDate,
                        endDate = formState.endDate
                    )
                    viewModel.insertTrip(trip)
                    onTripSaved()
                }
            ) {
                Text(stringResource(R.string.save_button_label))
            }
        }
    }
}


