package chkan.ua.domain.usecases.auth

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInAnonymouslyUseCase @Inject constructor(
    private val authManager: AuthManager
) {
    suspend operator fun invoke(): Boolean = authManager.signIn()
}
