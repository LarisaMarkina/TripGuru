package com.example.tripguru.presentation.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tripguru.R
import com.example.tripguru.presentation.composables.rememberDatePickerDialog
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TripFormScreen(
    tripId: Long? = null,
    viewModel: TripViewModel = hiltViewModel(),
    onNavigateBackWithResult: (String) -> Unit,
    onCancel: () -> Unit
) {

    // W przypadku edycji, pobierz podróż
    LaunchedEffect(key1 = tripId) {
        if (tripId != null) {
            viewModel.loadTripDetails(tripId)
        }
    }

    val trip by viewModel.selectedTripDetails.collectAsState()

    LaunchedEffect(trip) {
        trip?.let { viewModel.loadTripForEditing(it) }
    }

    val formUiState by viewModel.addTripFormState.collectAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Stany do kontrolowania widoczności DatePickerDialog
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Utwórz instancje DatePickerDialog dla daty rozpoczęcia i zakończenia
    val startDatePickerDialog = rememberDatePickerDialog(
        initialDate = viewModel.getInitialCalendarForDatePicker(formUiState.selectedStartDateMillis),
        onDateSelected = { year, month, dayOfMonth ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                clear()
                set(year, month, dayOfMonth)
            }
            viewModel.onStartDateSelected(calendar.timeInMillis)
            showStartDatePicker = false
        },
        onDismiss = { showStartDatePicker = false }
    )

    val endDatePickerDialog = rememberDatePickerDialog(
        initialDate = viewModel.getInitialCalendarForDatePicker(formUiState.selectedEndDateMillis),
        onDateSelected = { year, month, dayOfMonth ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                clear()
                set(year, month, dayOfMonth)
            }
            viewModel.onEndDateSelected(calendar.timeInMillis)
            showEndDatePicker = false
        },
        onDismiss = { showEndDatePicker = false }
    )

    // Nasłuchiwanie na eventy z ViewModelu
    LaunchedEffect(key1 = Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AddTripEvent.SaveError -> {
                    val errorMessage = if (event.messageResId != null) {
                        context.getString(event.messageResId)
                    } else {
                        event.customMessage ?: context.getString(R.string.msg_error_unknown)
                    }
                    snackBarHostState.showSnackbar(
                        message = errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }

                is AddTripEvent.SaveSuccessAndPrepareToNavigateBack -> {
                    keyboardController?.hide()
                    viewModel.clearFormState()
                    onNavigateBackWithResult(context.getString(R.string.msg_success_trip_saved))
                }
            }
        }
    }

    LaunchedEffect(showStartDatePicker) {
        if (showStartDatePicker) {
            startDatePickerDialog.show()
            val initialCalendar =
                viewModel.getInitialCalendarForDatePicker(formUiState.selectedStartDateMillis)
            startDatePickerDialog.updateDate(
                initialCalendar.get(Calendar.YEAR),
                initialCalendar.get(Calendar.MONTH),
                initialCalendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    LaunchedEffect(showEndDatePicker) {
        if (showEndDatePicker) {
            endDatePickerDialog.show()
            val initialCalendar =
                viewModel.getInitialCalendarForDatePicker(formUiState.selectedEndDateMillis)
            endDatePickerDialog.updateDate(
                initialCalendar.get(Calendar.YEAR),
                initialCalendar.get(Calendar.MONTH),
                initialCalendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    // Główny ekran
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {

            // Nagłówek ekranu
            TopAppBar(
                title = { Text(stringResource(R.string.title_add_trip)) },
                navigationIcon = {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        viewModel.clearFormState()
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
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (trip == null && tripId != null) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Nazwa
                    OutlinedTextField(
                        value = formUiState.name,
                        onValueChange = { viewModel.onNameChanged(it) },
                        label = { Text(stringResource(R.string.field_trip_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = formUiState.nameError != null,
                        supportingText = {
                            formUiState.nameError?.let { errorResId ->
                                Text(
                                    text = stringResource(errorResId),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    // Cel
                    OutlinedTextField(
                        value = formUiState.destination,
                        onValueChange = { viewModel.onDestinationChanged(it) },
                        label = { Text(stringResource(R.string.field_trip_destination)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Data rozpoczęcia
                    OutlinedTextField(
                        value = formUiState.startDateDisplay,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.field_trip_date_start)) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                focusManager.clearFocus()
                                showStartDatePicker = true
                            },
                        trailingIcon = {
                            Row {
                                // Usuń datę
                                if (formUiState.selectedStartDateMillis != null) {
                                    IconButton(onClick = { viewModel.clearStartDate() }) {
                                        Icon(
                                            Icons.Filled.Clear,
                                            contentDescription = stringResource(R.string.content_desc_clear_start_date)
                                        )
                                    }
                                }
                                // Wybierz datę
                                IconButton(onClick = {
                                    focusManager.clearFocus()
                                    showStartDatePicker = true
                                }) {
                                    Icon(
                                        Icons.Filled.DateRange,
                                        contentDescription = stringResource(R.string.content_desc_select_start_date)
                                    )
                                }
                            }
                        },
                        isError = formUiState.startDateError != null,
                        supportingText = {
                            formUiState.startDateError?.let {
                                Text(
                                    stringResource(it),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    // Data zakończenia
                    OutlinedTextField(
                        value = formUiState.endDateDisplay,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.field_trip_date_end)) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                focusManager.clearFocus()
                                showEndDatePicker = true
                            },
                        trailingIcon = {
                            Row {
                                // Usuń datę
                                if (formUiState.selectedEndDateMillis != null) {
                                    IconButton(onClick = { viewModel.clearEndDate() }) {
                                        Icon(
                                            Icons.Filled.Clear,
                                            contentDescription = stringResource(R.string.content_desc_clear_end_date)
                                        )
                                    }
                                }
                                // Wybierz datę
                                IconButton(onClick = {
                                    focusManager.clearFocus()
                                    showEndDatePicker = true
                                }) {
                                    Icon(
                                        Icons.Filled.DateRange,
                                        contentDescription = stringResource(R.string.content_desc_select_end_date)
                                    )
                                }
                            }
                        },
                        isError = formUiState.endDateError != null,
                        supportingText = {
                            formUiState.endDateError?.let {
                                Text(
                                    stringResource(it),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    // Liczba uczestników
                    OutlinedTextField(
                        value = formUiState.participantsNumber,
                        onValueChange = { viewModel.onNumberOfPeopleChanged(it) },
                        label = { Text(stringResource(R.string.field_trip_number_of_participants)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        textStyle = TextStyle(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                        singleLine = true,
                        leadingIcon = { // Przycisk "-"
                            IconButton(
                                onClick = { viewModel.decrementNumberOfPeople() },
                                enabled = (formUiState.participantsNumber.toIntOrNull() ?: 1) > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = stringResource(R.string.content_desc_decrement_participants)
                                )
                            }
                        },
                        trailingIcon = { // Przycisk "+"
                            IconButton(
                                onClick = { viewModel.incrementNumberOfPeople() },
                                enabled = (formUiState.participantsNumber.toIntOrNull() ?: 0) < 200
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = stringResource(R.string.content_desc_increment_participants)
                                )
                            }
                        },
                        isError = formUiState.participantsNumberError != null,
                        supportingText = {
                            formUiState.participantsNumberError?.let { errorResId ->
                                Text(
                                    text = stringResource(errorResId),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    // Opis
                    OutlinedTextField(
                        value = formUiState.description,
                        onValueChange = { viewModel.onDescriptionChanged(it) },
                        label = { Text(stringResource(R.string.field_trip_description)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { viewModel.attemptSaveTrip() }) // Opcjonalnie: zapisz po wciśnięciu "Done"
                    )

                    // Spacer, aby przyciski były na dole, jeśli jest dużo miejsca
                    Spacer(modifier = Modifier.weight(1f))

                    // Przycisk Zapisz
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.attemptSaveTrip()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = formUiState.canBeSaved && !formUiState.isSaving
                    ) {
                        if (formUiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(stringResource(R.string.button_label_save))
                        }
                    }
                }
            }
        }
    }
}