package com.android.cicdtesting.domain.usecase

import com.android.cicdtesting.domain.model.User
import com.android.cicdtesting.domain.repository.UserRepository

/**
 * Fetches a [User] by id.
 *
 * Declared as an interface so tests can mock it trivially:
 * `val useCase = mockk<GetUserUseCase>()`.
 */
fun interface GetUserUseCase {
    suspend operator fun invoke(id: String): User?
}

/**
 * Default implementation backed by a [UserRepository].
 */
class GetUserUseCaseImpl(
    private val repository: UserRepository,
) : GetUserUseCase {
    override suspend fun invoke(id: String): User? {
        require(id.isNotBlank()) { "id must not be blank" }
        return repository.findById(id)
    }
}
