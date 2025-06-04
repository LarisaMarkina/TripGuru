package com.example.tripguru.presentation.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripguru.R
import com.example.tripguru.data.model.Trip
import com.example.tripguru.domain.usecase.trip.TripUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing trip data and interacting with trip use cases.
 *
 * This ViewModel provides access to a list of trips and functions to perform CRUD operations
 * (Create, Read, Update, Delete) on trips. It uses [HiltViewModel] for dependency injection
 * and [viewModelScope] for coroutine management.
 *
 * @property tripUseCases The use cases responsible for handling trip-related business logic.
 */
@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripUseCases: TripUseCases
) : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _addTripFormState = MutableStateFlow(AddTripFormUiState())
    val addTripFormState: StateFlow<AddTripFormUiState> = _addTripFormState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddTripEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // --- Funkcje do aktualizacji pól ---
    fun onNameChanged(newName: String) {
        _addTripFormState.update { currentState ->
            val error = validateName(newName)
            currentState.copy(
                name = newName,
                nameError = error,
                canBeSaved = canFormBeSaved(
                    name = newName,
                    nameError = error,
                )
            )
        }
    }

    fun onDestinationChanged(newDestination: String) {
        _addTripFormState.update { currentState ->
            currentState.copy(
                destination = newDestination,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError
                )
            )
        }
    }

    fun onStartDateChanged(newStartDate: String) {
        _addTripFormState.update { currentState ->
            currentState.copy(
                startDate = newStartDate,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError
                )
            )
        }
    }

    fun onEndDateChanged(newEndDate: String) {
        _addTripFormState.update { currentState ->
            currentState.copy(
                endDate = newEndDate,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError
                )
            )
        }
    }

    fun onDescriptionChanged(newDescription: String) {
        _addTripFormState.update { currentState ->
            currentState.copy(
                description = newDescription,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError
                )
            )
        }
    }


    // --- Funkcje walidacyjne (zwracają ID zasobu stringu lub null) ---
    private fun validateName(name: String): Int? {
        if (name.isBlank()) {
            return R.string.error_name_empty
        }
        return null
    }

    /**
     * Funkcja pomocnicza do sprawdzania, czy formularz jest gotowy do zapisu
     *
     * @param name
     * @param nameError
     * @return
     */
    private fun canFormBeSaved(
        name: String, nameError: Int?
    ): Boolean {
        return name.isNotBlank() && nameError == null
    }

    /**
     *  Wyczyszczenie stanu formularza (np. po pomyślnym zapisie lub anulowaniu)
     *
     */
    fun clearFormState() {
        _addTripFormState.value = AddTripFormUiState()
    }

    /**
     * Funkcja do próby zapisu podróży
     */
    fun attemptSaveTrip() {
        val currentName = _addTripFormState.value.name
        val currentDestination = _addTripFormState.value.destination
        val currentStartDate = _addTripFormState.value.startDate
        val currentEndDate = _addTripFormState.value.endDate
        val currentDescription = _addTripFormState.value.description

        val nameValidationError = validateName(currentName)

        _addTripFormState.update { currentState ->
            currentState.copy(
                nameError = nameValidationError,
                canBeSaved = canFormBeSaved(currentName, nameValidationError)
            )
        }

        if (_addTripFormState.value.canBeSaved) {
            _addTripFormState.update { it.copy(isSaving = true) }
            viewModelScope.launch {
                try {
                    val trip = Trip(
                        name = currentName.trim(),
                        destination = currentDestination.trim(),
                        startDate = currentStartDate,
                        endDate = currentEndDate,
                        description = currentDescription.trim()
                    )
                    tripUseCases.addTrip(trip)
                    _eventFlow.emit(AddTripEvent.SaveSuccessAndPrepareToNavigateBack)
                } catch (e: Exception) {
                    _eventFlow.emit(
                        AddTripEvent.SaveError(
                            messageResId = R.string.error_saving_trip,
                            customMessage = e.localizedMessage
                        )
                    )
                    // Log.e("TripViewModel", "Error saving trip", e)
                } finally {
                    _addTripFormState.update { it.copy(isSaving = false) }
                }
            }
        }
    }

    fun loadTrips() {
        viewModelScope.launch {
            tripUseCases.getTrips().collect {
                _trips.value = it
            }
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            tripUseCases.deleteTrip(trip)
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            tripUseCases.updateTrip(trip)
        }
    }

    suspend fun getTripById(id: Long): Trip? {
        return tripUseCases.getTripById(id)
    }
}

data class AddTripFormUiState(
    val name: String = "",
    val nameError: Int? = null,
    val destination: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",

    val isSaving: Boolean = false,
    val canBeSaved: Boolean = false // Czy formularz jest gotowy do zapisu
)

/**
 * Klasa zapieczętowana dla różnych typów eventów
 *
 * @constructor Create empty Add trip event
 */
sealed class AddTripEvent {
    object SaveSuccessAndPrepareToNavigateBack : AddTripEvent()
    data class SaveError(val messageResId: Int?, val customMessage: String? = null) : AddTripEvent()
}