package se.supernovait.doobypro.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import se.supernovait.app.core.domain.crash.CrashReporter
import se.supernovait.app.core.domain.logging.Logger
import se.supernovait.doobypro.domain.manager.OrderManager
import se.supernovait.doobypro.domain.util.LogTags

class OrderAlertWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {
    private val logger: Logger by inject()
    private val crashReporter: CrashReporter by inject()
    private val orderManager: OrderManager by inject()

    override suspend fun doWork(): Result {
        return try {
            logger.info("OrderAlertWorker started processing order alerts", tag = LogTags.ORDER_ALERT_WORKER)
            orderManager.checkAndNotifyOrderAlerts()
            logger.info("OrderAlertWorker completed successfully", tag = LogTags.ORDER_ALERT_WORKER)
            Result.success()
        } catch (e: Exception) {
            logger.error("OrderAlertWorker failed, requesting retry", e, tag = LogTags.ORDER_ALERT_WORKER)
            crashReporter.recordException(e, mapOf("worker" to "OrderAlertWorker"))
            Result.retry()
        }
    }
}
