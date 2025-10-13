package chkan.ua.data.di

import chkan.ua.data.sources.firestore.AuthManagerImpl
import chkan.ua.domain.Analytics
import chkan.ua.domain.Logger
import chkan.ua.domain.usecases.auth.AuthManager
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthManager(
        auth: FirebaseAuth,
        logger: Logger,
        analytics: Analytics
    ): AuthManager {
        return AuthManagerImpl(auth,logger,analytics)
    }
}
