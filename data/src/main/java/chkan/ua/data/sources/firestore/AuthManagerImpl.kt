package chkan.ua.data.sources.firestore

import chkan.ua.domain.Analytics
import chkan.ua.domain.Logger
import chkan.ua.domain.usecases.auth.AuthManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManagerImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val logger: Logger,
    private val analytics: Analytics,
    ) : AuthManager {

    override suspend fun signIn(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (auth.currentUser != null) {
                auth.currentUser?.uid?.let { userId ->
                    analytics.setUserId(userId)
                }
                return@withContext true
            }

            val result = auth.signInAnonymously().await()

            result.user?.uid?.let { userId ->
                analytics.setUserId(userId)
            }

            true

        } catch (e: Exception) {
            logger.e(e,"FirebaseAuth e:$e")
            false
        }
    }

    override suspend fun getCurrentUserId(): String? = auth.currentUser?.uid
}
