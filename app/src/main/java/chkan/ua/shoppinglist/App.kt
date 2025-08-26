package chkan.ua.shoppinglist

import android.app.Application
import chkan.ua.shoppinglist.utils.CrashlyticsTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        super.onCreate()

        if (!BuildConfig.DEBUG) {
            Timber.plant(CrashlyticsTree())
        } else {
            Timber.plant(Timber.DebugTree())
        }
    }
}