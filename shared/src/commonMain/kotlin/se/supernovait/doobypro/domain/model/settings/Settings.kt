package se.supernovait.doobypro.domain.model.settings

import kotlinx.serialization.Serializable
import se.supernovait.doobypro.domain.model.settings.common.CommonSettings
import se.supernovait.doobypro.domain.model.settings.notification.NotificationSettings
import se.supernovait.doobypro.domain.model.settings.order.OrderSettings
import se.supernovait.doobypro.domain.model.settings.printer.PrinterSettings
import se.supernovait.doobypro.domain.model.settings.receipt.ReceiptSettings

@Serializable
data class Settings(
    val common: CommonSettings = CommonSettings(),
    val order: OrderSettings = OrderSettings(),
    val receipt: ReceiptSettings = ReceiptSettings(),
    val printer: PrinterSettings = PrinterSettings(),
    val notifications: NotificationSettings = NotificationSettings()
)
