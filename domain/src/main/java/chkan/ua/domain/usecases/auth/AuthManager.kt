package chkan.ua.domain.usecases.auth

interface AuthManager {
    suspend fun signIn(): Boolean
    suspend fun getCurrentUserId(): String?
}