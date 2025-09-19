package com.chkan.billing.di

import android.content.Context
import com.chkan.billing.core.BillingLogger
import com.chkan.billing.data.BillingRepositoryImpl
import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.service.SubscriptionBillingService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BillingModule {

    @Binds
    @Singleton
    abstract fun bindBillingRepository(
        billingRepositoryImpl: BillingRepositoryImpl
    ): BillingRepository

    companion object {
        @Provides
        @Singleton
        fun provideBillingService(
            @ApplicationContext context: Context,
            @Dispatcher(DispatcherType.IO) ioDispatcher: CoroutineDispatcher,
            @ApplicationScope scope: CoroutineScope,
            logger: BillingLogger
        ): SubscriptionBillingService {
            return SubscriptionBillingService(context, ioDispatcher, scope, logger)
        }
    }
}
