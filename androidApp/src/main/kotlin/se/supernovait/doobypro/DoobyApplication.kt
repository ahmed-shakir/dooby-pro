package se.supernovait.doobypro

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import se.supernovait.doobypro.di.initKoin
import se.supernovait.doobypro.worker.OrderAlertWorker
import java.util.concurrent.TimeUnit

class DoobyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@DoobyApplication)
            androidLogger()
        }
        setupBackgroundWorker()
    }

    private fun setupBackgroundWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<OrderAlertWorker>(4, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "OrderAlertsPeriodicWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
