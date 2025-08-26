package chkan.ua.shoppinglist.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN) {
            crashlytics.log(message)
            tag?.let { crashlytics.setCustomKey("NON-FATAL", it) }
        }

        t?.let { crashlytics.recordException(it) }
    }
}