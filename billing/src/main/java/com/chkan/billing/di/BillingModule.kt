package com.chkan.billing.di

import android.content.Context
import com.chkan.billing.data.BillingRepositoryImpl
import com.chkan.billing.domain.BillingRepository
import com.chkan.billing.service.SubscriptionBillingService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        fun provideBillingService(@ApplicationContext context: Context): SubscriptionBillingService {
            return SubscriptionBillingService(context)
        }
    }
}
