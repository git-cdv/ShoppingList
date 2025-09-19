package chkan.ua.shoppinglist.di

import chkan.ua.domain.Logger
import com.chkan.billing.core.BillingLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    fun provideBillingLogger(logger: Logger): BillingLogger {
        return object : BillingLogger {
            override fun d(tag: String, message: String) {
                logger.d(tag, message)
            }

            override fun e(e: Exception, message: String?) {
                logger.e(e, message)
            }

            override fun e(e: Throwable, message: String?) {
                logger.e(e, message)
            }
        }
    }
}
