package com.example.tripguru.presentation.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tripguru.R
import com.example.tripguru.presentation.composables.rememberDatePickerDialog
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddTripScreen(
    viewModel: TripViewModel = hiltViewModel(),
    onNavigateBackWithResult: (String) -> Unit,
    onCancel: () -> Unit
) {
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
                        event.customMessage ?: context.getString(R.string.unknown_error)
                    }
                    snackBarHostState.showSnackbar(
                        message = errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }

                is AddTripEvent.SaveSuccessAndPrepareToNavigateBack -> {
                    keyboardController?.hide()
                    viewModel.clearFormState()
                    onNavigateBackWithResult(context.getString(R.string.trip_saved_successfully))
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
                title = { Text(stringResource(R.string.add_trip_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        viewModel.clearFormState()
                        onCancel()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_label)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Nazwa
            OutlinedTextField(
                value = formUiState.name,
                onValueChange = { viewModel.onNameChanged(it) },
                label = { Text(stringResource(R.string.tripe_name_field)) },
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
                label = { Text(stringResource(R.string.trip_destination_field)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Data rozpoczęcia
            OutlinedTextField(
                value = formUiState.startDateDisplay,
                onValueChange = {},
                label = { Text(stringResource(R.string.trip_date_start_field)) },
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
                                    contentDescription = stringResource(R.string.clear_start_date_description)
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
                                contentDescription = stringResource(R.string.select_start_date_description)
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
                label = { Text(stringResource(R.string.trip_date_end_field)) },
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
                                    contentDescription = stringResource(R.string.clear_end_date_description)
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
                                contentDescription = stringResource(R.string.select_end_date_description)
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

            // Opis
            OutlinedTextField(
                value = formUiState.description,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                label = { Text(stringResource(R.string.trip_description_field)) },
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
                    Text(stringResource(R.string.save_button_label))
                }
            }
        }
    }
}


