package se.supernovait.doobypro

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import se.supernovait.doobypro.di.initKoin

class DoobyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@DoobyApplication)
            androidLogger()
        }
    }
}
