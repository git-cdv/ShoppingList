package chkan.ua.data.sources.firestore

import android.util.Log
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
            Log.d("SHARE", "auth.currentUser: ${auth.currentUser}")
            if (auth.currentUser != null) return@withContext true
            auth.signInAnonymously().await()
            Log.d("SHARE", "auth.currentUser AFTER: ${auth.currentUser}")
            true
        } catch (e: Exception) {
            Log.d("SHARE", "auth.currentUser ERROR: $e")
            false
        }
    }

    override suspend fun getCurrentUserId(): String? = auth.currentUser?.uid
}
