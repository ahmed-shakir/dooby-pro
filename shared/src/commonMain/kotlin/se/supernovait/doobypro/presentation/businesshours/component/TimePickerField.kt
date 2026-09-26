package se.supernovait.doobypro.presentation.businesshours.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import kotlinx.datetime.LocalTime

@Composable
fun TimePickerField(
    value: LocalTime,
    onValueChange: (LocalTime) -> Unit
) {
    val initialText = "${value.hour.toString().padStart(2, '0')}:${value.minute.toString().padStart(2, '0')}"
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(initialText, TextRange(initialText.length)))
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val oldText = textFieldValue.text
            val newText = newValue.text

            // Extract digits only (max 4)
            val digits = newText.filter { it.isDigit() }.take(4)

            // Format digits as HH:MM
            val formatted = when {
                digits.length <= 2 -> digits
                else -> "${digits.take(2)}:${digits.drop(2)}"
            }

            // Calculate new cursor position intelligently
            val newCursorPos = when {
                formatted.length < oldText.length -> {
                    // Deletion happened
                    newValue.selection.end.coerceIn(0, formatted.length)
                }
                digits.length == 3 && oldText.length == 2 -> {
                    // Just crossed the colon (typed 3rd digit)
                    4 // right after the colon and 1st minute digit
                }
                else -> {
                    formatted.length.coerceIn(0, formatted.length)
                }
            }

            textFieldValue = TextFieldValue(formatted, TextRange(newCursorPos))

            // Parse and notify
            if (digits.length >= 3) {
                val hour = digits.take(2).toIntOrNull()?.coerceIn(0, 23) ?: 0
                val minuteStr = digits.drop(2).padEnd(2, '0')
                val minute = minuteStr.toIntOrNull()?.coerceIn(0, 59) ?: 0
                onValueChange(LocalTime(hour, minute))
            } else if (digits.length == 2) {
                val hour = digits.toIntOrNull()?.coerceIn(0, 23) ?: 0
                onValueChange(LocalTime(hour, value.minute))
            }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text("HH:MM") },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}
