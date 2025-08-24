package chkan.ua.data.sources.firestore

import chkan.ua.domain.usecases.auth.AuthManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManagerImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthManager {

    override suspend fun signIn(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (auth.currentUser != null) return@withContext true
            auth.signInAnonymously().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getCurrentUserId(): String? = auth.currentUser?.uid
}
