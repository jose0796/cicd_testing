package com.android.cicdtesting.domain.repository

import com.android.cicdtesting.domain.model.User

/**
 * Data source abstraction. Kept as an interface so it can be swapped/mocked.
 */
interface UserRepository {
    suspend fun findById(id: String): User?
}
