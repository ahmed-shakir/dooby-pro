package se.supernovait.doobypro.presentation.businesshours.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.business_hours_24_hours
import doobypro.shared.generated.resources.business_hours_closed
import doobypro.shared.generated.resources.business_hours_custom
import doobypro.shared.generated.resources.business_hours_edit_day
import doobypro.shared.generated.resources.business_hours_hours_label
import doobypro.shared.generated.resources.business_hours_open
import doobypro.shared.generated.resources.business_hours_status_label
import doobypro.shared.generated.resources.label_cancel
import doobypro.shared.generated.resources.label_save
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import se.supernovait.app.core.ui.component.action.SupernovaButton
import se.supernovait.app.core.ui.component.action.SupernovaOutlinedButton
import se.supernovait.app.core.ui.component.selection.SupernovaStatusToggle
import se.supernovait.app.core.ui.component.text.SupernovaLabel
import se.supernovait.app.core.ui.theme.spacing
import se.supernovait.doobypro.domain.model.company.DayHours

@Composable
fun DayHoursEditorDialog(
    day: DayOfWeek,
    currentHours: DayHours,
    onSave: (DayHours) -> Unit,
    onDismiss: () -> Unit,
    isSaving: Boolean
) {
    var isOpen by remember {
        mutableStateOf(currentHours !is DayHours.Closed)
    }
    var is24Hours by remember {
        mutableStateOf(currentHours is DayHours.OpenAllDay)
    }
    var openTime by remember {
        mutableStateOf(
            (currentHours as? DayHours.OpenRange)?.openTime ?: LocalTime(9, 0)
        )
    }
    var closeTime by remember {
        mutableStateOf(
            (currentHours as? DayHours.OpenRange)?.closeTime ?: LocalTime(17, 0)
        )
    }

    val dayName = day.name.lowercase().replaceFirstChar { it.uppercase() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isSaving,
            dismissOnClickOutside = !isSaving
        )
    ) {
        Card(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(MaterialTheme.shapes.medium),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.large),
            ) {
                SupernovaLabel(
                    text = stringResource(Res.string.business_hours_edit_day, dayName),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                SupernovaStatusToggle(
                    isOpen = isOpen,
                    onOpenChange = { isOpen = it },
                    title = stringResource(Res.string.business_hours_status_label),
                    openLabel = stringResource(Res.string.business_hours_open),
                    closedLabel = stringResource(Res.string.business_hours_closed)
                )

                AnimatedVisibility(
                    visible = isOpen,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                    ) {
                        SupernovaStatusToggle(
                            isOpen = is24Hours,
                            onOpenChange = { is24Hours = it },
                            title = stringResource(Res.string.business_hours_hours_label),
                            openLabel = stringResource(Res.string.business_hours_24_hours),
                            closedLabel = stringResource(Res.string.business_hours_custom)
                        )

                        AnimatedVisibility(
                            visible = isOpen && !is24Hours,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            TimeRangePicker(
                                openTime = openTime,
                                closeTime = closeTime,
                                onOpenTimeChange = { openTime = it },
                                onCloseTimeChange = { closeTime = it }
                            )
                        }
                    }
                }

                HorizontalDivider()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.small)
                ) {
                    SupernovaOutlinedButton(
                        label = stringResource(Res.string.label_cancel),
                        onClick = onDismiss,
                        enabled = !isSaving,
                        modifier = Modifier.weight(1f)
                    )

                    SupernovaButton(
                        label = stringResource(Res.string.label_save),
                        onClick = {
                            val newHours = when {
                                !isOpen -> DayHours.Closed
                                is24Hours -> DayHours.OpenAllDay
                                else -> DayHours.OpenRange(openTime, closeTime)
                            }
                            onSave(newHours)
                        },
                        enabled = !isSaving,
                        loading = isSaving,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
