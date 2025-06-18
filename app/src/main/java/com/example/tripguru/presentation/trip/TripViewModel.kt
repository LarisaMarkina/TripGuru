package com.example.tripguru.presentation.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripguru.R
import com.example.tripguru.data.model.Trip
import com.example.tripguru.domain.usecase.trip.TripUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class AddTripFormUiState(
    val name: String = "",
    val nameError: Int? = null,

    val destination: String = "",

    val participantsNumber: String = "1",
    val participantsNumberError: Int? = null,

    val selectedStartDateMillis: Long? = null,
    val startDateDisplay: String = "",
    val startDateError: Int? = null,

    val selectedEndDateMillis: Long? = null,
    val endDateDisplay: String = "",
    val endDateError: Int? = null,

    val description: String = "",

    val isSaving: Boolean = false,
    val canBeSaved: Boolean = false,
    val editingTripId: Long? = null
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

@HiltViewModel
class TripViewModel @Inject constructor(
    private val tripUseCases: TripUseCases
) : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _selectedTripId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedTripDetails: StateFlow<Trip?> =
        _selectedTripId.filterNotNull().flatMapLatest { id ->
            tripUseCases.getTripById(id).catch { exception ->
                emit(null)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null // Początkowa wartość, UI pokaże ładowanie
        )

    private val _addTripFormState = MutableStateFlow(AddTripFormUiState())
    val addTripFormState: StateFlow<AddTripFormUiState> = _addTripFormState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddTripEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())


// --------- Funkcje do aktualizacji stanu formularza ---------
    /**
     * Funkcja, reagująca na zmianę nazwy
     *
     * @param newName - nowa nazwa podróży
     */
    fun onNameChanged(newName: String) {
        _addTripFormState.update { currentState ->
            val error = validateName(newName)
            currentState.copy(
                name = newName, nameError = error, canBeSaved = canFormBeSaved(
                    name = newName,
                    nameError = error,
                    startDateError = currentState.startDateError,
                    endDateError = currentState.endDateError,
                    participantsNumberError = currentState.participantsNumberError
                )
            )
        }
    }

    /**
     * Funkcja, reagująca na zmianę celu podróży
     *
     * @param newDestination - nowy cel podróży
     */
    fun onDestinationChanged(newDestination: String) {
        _addTripFormState.update { currentState ->
            currentState.copy(
                destination = newDestination, canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError,
                    startDateError = currentState.startDateError,
                    endDateError = currentState.endDateError,
                    participantsNumberError = currentState.participantsNumberError
                )
            )
        }
    }

    /**
     * Funkcja, reagująca na zmianę daty początku podróży
     *
     * @param dateMillis - data początku podróży w ms (UNIX)
     */
    fun onStartDateSelected(dateMillis: Long) {
        val formattedDate = dateFormatter.format(Date(dateMillis))
        _addTripFormState.update { currentState ->
            val endDateMillis = currentState.selectedEndDateMillis
            val startDateValError = validateSpecificDate(dateMillis, endDateMillis, true)
            val endDateValError = validateSpecificDate(endDateMillis, dateMillis, false)

            currentState.copy(
                startDateDisplay = formattedDate,
                selectedStartDateMillis = dateMillis,
                startDateError = startDateValError,
                endDateError = endDateValError,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError,
                    startDateError = startDateValError,
                    endDateError = endDateValError,
                    participantsNumberError = currentState.participantsNumberError
                )
            )
        }
    }

    /**
     * Funkcja, reagująca na zmianę daty końca podróży
     *
     * @param dateMillis - data końca podróży w ms (UNIX)
     */
    fun onEndDateSelected(dateMillis: Long) {
        val formattedDate = dateFormatter.format(Date(dateMillis))
        _addTripFormState.update { currentState ->
            val startDateMillis = currentState.selectedStartDateMillis
            val endDateValError = validateSpecificDate(dateMillis, startDateMillis, false)
            val startDateValError = validateSpecificDate(startDateMillis, dateMillis, true)

            currentState.copy(
                endDateDisplay = formattedDate,
                selectedEndDateMillis = dateMillis,
                endDateError = endDateValError,
                startDateError = startDateValError,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError,
                    startDateError = startDateValError,
                    endDateError = endDateValError,
                    participantsNumberError = currentState.participantsNumberError
                )
            )
        }
    }

    /**
     * Funkcja, reagująca na zmianę liczby uczestników podróży
     *
     * @param newParticipantsNumber
     */
    fun onNumberOfPeopleChanged(newParticipantsNumber: String) {
        _addTripFormState.update { currentState ->
            val error = validateParticipantsNumber(newParticipantsNumber)
            currentState.copy(
                participantsNumber = newParticipantsNumber,
                participantsNumberError = error,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError,
                    startDateError = currentState.startDateError,
                    endDateError = currentState.endDateError,
                    participantsNumberError = error,
                )
            )
        }
    }

    /**
     * Funkcja, reagująca na zmianę opisu podróży
     *
     * @param newDescription - nowy opis podróży
     */
    fun onDescriptionChanged(newDescription: String) {
        _addTripFormState.update { currentState ->
            currentState.copy(
                description = newDescription, canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError,
                    startDateError = currentState.startDateError,
                    endDateError = currentState.endDateError,
                    participantsNumberError = currentState.participantsNumberError
                )
            )
        }
    }

// --------- Funkcje walidacyjne (zwracają ID zasobu stringu lub null) ---------

    /**
     * Funkcja walidująca nazwę podróży
     *
     * @param name - nazwa podróży
     * @return - Błąd nazwy podróży lub null
     */
    private fun validateName(name: String): Int? {
        if (name.isBlank()) {
            return R.string.msg_error_name_empty
        }
        return null
    }

    /**
     * Funkcja walidująca obie daty (początku i końca podróży) względem siebie
     *
     * @param startDateMillis - data początku podróży w ms (UNIX)
     * @param endDateMillis - data końcu podróży w ms (UNIX)
     * @return - Zwraca parę błędów (błąd dla startDate, błąd dla endDate).
     */
    private fun validateDates(startDateMillis: Long?, endDateMillis: Long?): Pair<Int?, Int?> {
        var startDateError: Int? = null
        var endDateError: Int? = null

        when {
            startDateMillis != null && endDateMillis != null -> {
                if (startDateMillis > endDateMillis) {
                    endDateError = R.string.msg_error_end_date_before_start_date
                }
            }

            startDateMillis == null && endDateMillis != null -> {
                startDateError = R.string.msg_error_empty_start_date
                endDateError = null
            }

            startDateMillis != null && endDateMillis == null -> {
                endDateError = R.string.msg_error_empty_end_date
                startDateError = null
            }
        }
        return Pair(startDateError, endDateError)
    }

    /**
     * Funkcja pomocnicza do walidacji konkretnej daty w kontekście drugiej.
     * Używana głównie, gdy jedna data jest aktywnie zmieniana.
     *
     * @param dateToCheckMillis - data do walidacji
     * @param otherDateMillis - druga data do porównania
     * @param isCheckingStartDate - czy sprawdzamy datę rozpoczęcia
     * @return - Błąd daty lub null
     */
    private fun validateSpecificDate(
        dateToCheckMillis: Long?, otherDateMillis: Long?, isCheckingStartDate: Boolean
    ): Int? {
        val (startError, endError) = if (isCheckingStartDate) {
            validateDates(dateToCheckMillis, otherDateMillis)
        } else {
            validateDates(otherDateMillis, dateToCheckMillis)
        }
        return if (isCheckingStartDate) startError else endError
    }

    /**
     * Funkcja pomocnicza do walidacji liczby uczestników podróży
     *
     * @param participantsNumber - liczba uczestników podróży
     * @return - Błąd liczby uczestników lub null
     */
    private fun validateParticipantsNumber(participantsNumber: String): Int? {
        val number = participantsNumber.toIntOrNull()
        return when {
            number == null -> R.string.msg_error_invalid_number_format
            number < 1 -> R.string.msg_error_number_of_participants_too_low
            else -> null
        }
    }

// --------- Funkcje do obsługi wyboru dat ---------

    /**
     * Funkcja do czyszczenia daty początku podróży
     *
     */
    fun clearStartDate() {
        _addTripFormState.update { currentState ->
            val (startDateErr, endDateErr) = validateDates(null, currentState.selectedEndDateMillis)

            currentState.copy(
                startDateDisplay = "",
                selectedStartDateMillis = null,
                startDateError = startDateErr,
                endDateDisplay = currentState.endDateDisplay,
                selectedEndDateMillis = currentState.selectedEndDateMillis,
                endDateError = endDateErr,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError,
                    startDateError = startDateErr,
                    endDateError = endDateErr,
                    participantsNumberError = currentState.participantsNumberError
                )
            )
        }
    }

    /**
     * Funkcja do czyszczenia daty końca podróży
     *
     */
    fun clearEndDate() {
        _addTripFormState.update { currentState ->
            val (startDateErr, endDateErr) = validateDates(
                currentState.selectedStartDateMillis, null
            )

            currentState.copy(
                startDateDisplay = currentState.startDateDisplay,
                selectedStartDateMillis = currentState.selectedStartDateMillis,
                startDateError = startDateErr,
                endDateDisplay = "",
                selectedEndDateMillis = null,
                endDateError = endDateErr,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError,
                    startDateError = startDateErr,
                    endDateError = endDateErr,
                    participantsNumberError = currentState.participantsNumberError
                )
            )
        }
    }

    /**
     * Funkcja do inicjalizacji kalendarza do wyboru dat
     *
     * @param dateMillis
     * @return
     */
    fun getInitialCalendarForDatePicker(dateMillis: Long?): Calendar {
        val calendar = Calendar.getInstance()
        dateMillis?.let {
            calendar.timeInMillis = it
        }
        return calendar
    }

    // --------- Funkcje do obsługi wyboru liczby uczestników ---------

    /**
     * Funkcja, zwiększająca o 1 liczbę osób w podróży
     *
     */
    fun incrementNumberOfPeople() {
        _addTripFormState.update { currentState ->
            val currentNumber = currentState.participantsNumber.toIntOrNull() ?: 1
            val newNumber = (currentNumber + 1).coerceAtMost(1000) // Ogranicz do 1000
            val newNumberString = newNumber.toString()
            val error = validateParticipantsNumber(newNumberString)
            currentState.copy(
                participantsNumber = newNumberString,
                participantsNumberError = error,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError,
                    startDateError = currentState.startDateError,
                    endDateError = currentState.endDateError,
                    participantsNumberError = error,
                )
            )
        }
    }

    /**
     * Funkcja, zmniejszająca o 1 liczbę osób w podróży
     *
     */
    fun decrementNumberOfPeople() {
        _addTripFormState.update { currentState ->
            val currentNumber = currentState.participantsNumber.toIntOrNull() ?: 1
            val newNumber = (currentNumber - 1).coerceAtLeast(1) // Ogranicz do minimum 1
            val newNumberString = newNumber.toString()
            val error = validateParticipantsNumber(newNumberString)
            currentState.copy(
                participantsNumber = newNumberString,
                participantsNumberError = error,
                canBeSaved = canFormBeSaved(
                    name = currentState.name,
                    nameError = currentState.nameError,
                    startDateError = currentState.startDateError,
                    endDateError = currentState.endDateError,
                    participantsNumberError = error,
                )
            )
        }
    }

// --------- Funkcje do zapisu danych formularza ---------
    /**
     * Funkcja pomocnicza do sprawdzania, czy formularz jest gotowy do zapisu
     *
     * @param name - nazwa podróży
     * @param nameError - błąd nazwy podróży
     * @param startDateError - błąd daty rozpoczęcia
     * @param endDateError - błąd daty zakończenia
     * @param participantsNumberError - błąd liczby uczestników
     * @return - Czy formularz jest gotowy do zapisu
     */
    private fun canFormBeSaved(
        name: String, nameError: Int?,
        startDateError: Int?, endDateError: Int?,
        participantsNumberError: Int?
    ): Boolean {
        val basicValidation = name.isNotBlank() && nameError == null
        val dateValidation = startDateError == null && endDateError == null
        val participantsNumberValidation = participantsNumberError == null

        return basicValidation && dateValidation && participantsNumberValidation
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
     *
     */
    fun attemptSaveTrip() {
        val currentState = _addTripFormState.value

        val nameValidationError = validateName(currentState.name)
        val (startDateValError, endDateValError) = validateDates(
            currentState.selectedStartDateMillis, currentState.selectedEndDateMillis
        )
        val participantsNumberError = validateParticipantsNumber(currentState.participantsNumber)

        _addTripFormState.update {
            it.copy(
                nameError = nameValidationError,
                startDateError = startDateValError,
                endDateError = endDateValError,
                participantsNumberError = participantsNumberError,
                canBeSaved = canFormBeSaved(
                    name = it.name,
                    nameError = nameValidationError,
                    startDateError = startDateValError,
                    endDateError = endDateValError,
                    participantsNumberError = participantsNumberError
                )
            )
        }

        if (_addTripFormState.value.canBeSaved) {
            _addTripFormState.update { it.copy(isSaving = true) }
            viewModelScope.launch {
                try {
                    val trip = Trip(
                        id = currentState.editingTripId ?: 0,
                        name = currentState.name.trim(),
                        destination = currentState.destination.trim(),
                        participantsNumber = currentState.participantsNumber.toIntOrNull() ?: 1,
                        startDate = currentState.selectedStartDateMillis,
                        endDate = currentState.selectedEndDateMillis,
                        description = currentState.description.trim()
                    )

                    if (isEditingExistingTrip()) {
                        tripUseCases.updateTrip(trip)
                    } else {
                        tripUseCases.addTrip(trip)
                    }

                    _eventFlow.emit(AddTripEvent.SaveSuccessAndPrepareToNavigateBack)
                } catch (e: Exception) {
                    _eventFlow.emit(
                        AddTripEvent.SaveError(
                            messageResId = R.string.msg_error_saving_trip,
                            customMessage = e.localizedMessage
                        )
                    )
                } finally {
                    _addTripFormState.update { it.copy(isSaving = false) }
                }
            }
        }
    }

    private fun isEditingExistingTrip(): Boolean {
        return _addTripFormState.value.editingTripId != null
    }

    fun loadTripForEditing(trip: Trip) {
        val (startDateError, endDateError) = validateDates(trip.startDate, trip.endDate)
        val participantsNumberError = validateParticipantsNumber(trip.participantsNumber.toString())
        _addTripFormState.update {
            it.copy(
                editingTripId = trip.id,
                name = trip.name,
                destination = trip.destination ?: "",
                participantsNumber = trip.participantsNumber?.toString() ?: "1",
                participantsNumberError = participantsNumberError,
                selectedStartDateMillis = trip.startDate,
                startDateDisplay = trip.startDate?.let { millis -> dateFormatter.format(Date(millis)) }
                    ?: "",
                selectedEndDateMillis = trip.endDate,
                endDateDisplay = trip.endDate?.let { millis -> dateFormatter.format(Date(millis)) }
                    ?: "",
                description = trip.description ?: "",
                startDateError = startDateError,
                endDateError = endDateError,
                isSaving = false,
                canBeSaved = canFormBeSaved(
                    name = trip.name,
                    nameError = null,
                    startDateError = startDateError,
                    endDateError = endDateError,
                    participantsNumberError = participantsNumberError
                ))
        }
    }

    /**
     * Informuje ViewModel, które ID podróży ma zostać załadowane.
     * Uruchamia przepływ w selectedTripDetails.
     *
     * @param tripId - id podróży
     */
    fun loadTripDetails(tripId: Long) {
        _selectedTripId.value = tripId
    }

    fun clearSelectedTrip() {
        _selectedTripId.value = null
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
}

