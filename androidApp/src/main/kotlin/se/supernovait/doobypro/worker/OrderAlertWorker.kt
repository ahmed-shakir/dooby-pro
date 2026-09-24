package se.supernovait.doobypro.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import se.supernovait.doobypro.domain.manager.OrderManager

class OrderAlertWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val orderManager: OrderManager by inject()

    override suspend fun doWork(): Result {
        return try {
            orderManager.checkAndNotifyOrderAlerts()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
