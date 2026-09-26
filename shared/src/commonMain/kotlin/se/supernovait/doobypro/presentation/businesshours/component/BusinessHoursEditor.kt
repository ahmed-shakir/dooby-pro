package se.supernovait.doobypro.presentation.businesshours.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import se.supernovait.app.core.ui.component.loading.SupernovaLoadingIndicator
import se.supernovait.doobypro.domain.model.company.DayHours
import se.supernovait.doobypro.domain.usecase.defaultFormatDayHours
import se.supernovait.doobypro.presentation.businesshours.BusinessHoursEditorState

@Composable
fun BusinessHoursEditor(
    state: BusinessHoursEditorState,
    onDayHoursUpdate: (DayOfWeek, DayHours) -> Unit,
    modifier: Modifier = Modifier,
    formatDayHours: (DayHours) -> String = { defaultFormatDayHours(it) }
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                SupernovaLoadingIndicator()
            }
            return@Column
        }

        state.businessHours?.let { hours ->
            DayOfWeek.entries.forEach { day ->
                BusinessHoursDayEditorItem(
                    day = day,
                    dayHours = hours.getDayHours(day),
                    onEdit = { newHours ->
                        onDayHoursUpdate(day, newHours)
                    },
                    isSaving = state.isSaving,
                    formatDayHours = formatDayHours
                )
            }
        }
    }
}
