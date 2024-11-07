package chkan.ua.shoppinglist.di

import chkan.ua.shoppinglist.core.services.ErrorHandler
import chkan.ua.shoppinglist.core.services.ErrorHandlerBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler = ErrorHandlerBase()
}