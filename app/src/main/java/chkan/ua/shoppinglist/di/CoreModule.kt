package chkan.ua.shoppinglist.di

import chkan.ua.shoppinglist.core.services.ErrorHandler
import chkan.ua.shoppinglist.core.services.ErrorHandlerImpl
import chkan.ua.shoppinglist.core.services.SharedPreferencesService
import chkan.ua.shoppinglist.core.services.SharedPreferencesServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {

    @Singleton
    @Binds
    fun provideErrorHandler(impl: ErrorHandlerImpl): ErrorHandler

    @Singleton
    @Binds
    fun roomSource(impl: SharedPreferencesServiceImpl) : SharedPreferencesService
}