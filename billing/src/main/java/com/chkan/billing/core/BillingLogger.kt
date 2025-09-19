package com.chkan.billing.core

interface BillingLogger {
    fun d(tag: String, message: String)
    fun e(e: Exception, message: String? = null)
    fun e(e: Throwable, message: String? = null)
}

/*
BIND IN APP MODULE:
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
}*/
