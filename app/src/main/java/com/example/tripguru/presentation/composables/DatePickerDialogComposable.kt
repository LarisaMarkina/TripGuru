package com.example.tripguru.presentation.composables

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun rememberDatePickerDialog(
    initialDate: Calendar = Calendar.getInstance(),
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
    onDismiss: () -> Unit
): DatePickerDialog {
    val context = LocalContext.current
    val datePickerDialog = remember(initialDate, onDateSelected, onDismiss) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                onDateSelected(year, month, dayOfMonth)
            },
            initialDate.get(Calendar.YEAR),
            initialDate.get(Calendar.MONTH),
            initialDate.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener {
                onDismiss()
            }
        }
    }
    return datePickerDialog
}