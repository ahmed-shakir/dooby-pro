package se.supernovait.doobypro

import android.content.pm.ApplicationInfo
import se.supernovait.app.core.domain.util.applicationContext

actual val platformDebugMode: Boolean
    get() = try {
        val context = applicationContext
        (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    } catch (_: Exception) {
        true
    }
