package net.hitpromo.hitpromoworkstation.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.hitpromo.hitpromoworkstation.domain.model.AuthResult
import net.hitpromo.hitpromoworkstation.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for signing out the current user.
 */
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Execute the sign-out operation.
     *
     * @return Flow of AuthResult indicating the progress and result of the operation
     */
    operator fun invoke(): Flow<AuthResult<Unit>> = flow {
        try {
            emit(AuthResult.Loading)
            val result = authRepository.signOut()
            emit(result)
        } catch (e: Exception) {
            emit(AuthResult.Error("Failed to sign out", e))
        }
    }
}