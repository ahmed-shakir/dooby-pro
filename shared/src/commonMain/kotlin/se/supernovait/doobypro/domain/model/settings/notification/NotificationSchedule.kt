package se.supernovait.doobypro.domain.model.settings.notification

import doobypro.shared.generated.resources.Res
import doobypro.shared.generated.resources.notification_schedule_anytime
import doobypro.shared.generated.resources.notification_schedule_business_hours
import doobypro.shared.generated.resources.notification_schedule_daytime
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@Serializable
enum class NotificationSchedule(val label: StringResource) {
    BUSINESS_HOURS(Res.string.notification_schedule_business_hours),
    DAYTIME(Res.string.notification_schedule_daytime),
    ANYTIME(Res.string.notification_schedule_anytime)
}
