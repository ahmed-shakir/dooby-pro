package se.supernovait.doobypro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import se.supernovait.app.core.domain.initialization.AppInitializer
import se.supernovait.doobypro.presentation.app.App

class MainActivity : ComponentActivity() {
    private val appInitializer: AppInitializer by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        // Keep splash screen visible while initializing
        // It will automatically dismiss once initialization completes (Success or Error state)
        splashScreen.setKeepOnScreenCondition {
            appInitializer.isInitializing()
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Start app initialization in the background
        lifecycleScope.launch {
            appInitializer.initialize()
        }

        setContent {
            App()
        }
    }
}
