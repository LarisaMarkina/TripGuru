package com.example.tripguru.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A [VisualTransformation] that formats the input text as a date in the format "dd.MM.yyyy".
 * It automatically adds dots after the day and month parts as the user types.
 *
 * For example:
 * - Input "12" becomes "12."
 * - Input "1203" becomes "12.03."
 * - Input "12032023" becomes "12.03.2023"
 *
 * The transformation limits the input to a maximum of 8 digits, corresponding to "ddMMyyyy".
 */
class DateVisualTransformation() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return dateFilter(text)
    }
}

fun dateFilter(text: AnnotatedString): TransformedText {

    val originalText = text.text.filter { it.isDigit() }
    val trimmed = if (originalText.length >= 8) originalText.substring(0..7) else originalText
    val out = StringBuilder()

    for (i in trimmed.indices) {
        out.append(trimmed[i])
        // Dodaj kropkę po 2. i 4. cyfrze, jeśli nie jest to ostatnia cyfra
        if ((i == 1 || i == 3) && i < trimmed.length - 1) {
            out.append('.')
        }
    }
    val formattedText = out.toString()

    val offsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            var transformedOffset = offset

            if (offset > 1) { // Jeśli kursor jest za dd
                transformedOffset += 1 // Dodaj 1 za pierwszą kropkę
            }

            if (offset > 3) { // Jeśli kursor jest za dd.mm
                transformedOffset += 1 // Dodaj 1 za drugą kropkę
            }

            return transformedOffset.coerceAtMost(formattedText.length)
        }

        override fun transformedToOriginal(offset: Int): Int {
            var originalOffset = offset

            if (offset > 2) { // Jeśli kursor jest za dd.
                originalOffset -= 1 // Odejmij 1 za pierwszą kropkę
            }
            if (offset > 5) { // Jeśli kursor jest za dd.mm.
                originalOffset -= 1 // Odejmij 1 za drugą kropkę
            }

            return originalOffset.coerceAtMost(trimmed.length).coerceAtLeast(0)
        }
    }

    return TransformedText(AnnotatedString(formattedText), offsetTranslator)
}