package chkan.ua.shoppinglist.di

import chkan.ua.domain.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {

    @Provides
    @Singleton
    fun provideLogger(): Logger = TimberLogger()
}

class TimberLogger : Logger {
    override fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    override fun e(e: Exception, message: String?) {
        Timber.tag("ERROR").d("$e + message:$message")
        Timber.e(e, message)
    }

    override fun e(e: Throwable, message: String?) {
        Timber.tag("ERROR").d("$e + message:$message")
        Timber.e(e, message)
    }
}
