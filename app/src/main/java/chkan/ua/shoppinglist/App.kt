package chkan.ua.shoppinglist

import android.app.Application
import chkan.ua.shoppinglist.utils.CrashlyticsTree
import com.chkan.billing.domain.BillingRepository
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(){

    @Inject
    lateinit var billingRepository: BillingRepository

    override fun onCreate() {
        super.onCreate()

        if (!BuildConfig.DEBUG) {
            Timber.plant(CrashlyticsTree())
        } else {
            Timber.plant(Timber.DebugTree())
        }

        billingRepository.startConnection()
    }

    override fun onTerminate() {
        super.onTerminate()
        billingRepository.endConnection()
    }

}