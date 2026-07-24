package com.android.cicdtesting.feature

import com.android.cicdtesting.domain.model.User
import com.android.cicdtesting.domain.repository.UserRepository
import com.android.cicdtesting.domain.usecase.CalculateDiscountUseCaseImpl
import com.android.cicdtesting.domain.usecase.GetUserUseCaseImpl
import com.android.cicdtesting.domain.usecase.ValidateEmailUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Integration-style test: uses the real use case implementations and mocks
 * only the outer boundary (the repository). Complements [CheckoutServiceTest],
 * which mocks every use case individually.
 */
class CheckoutServiceIntegrationTest {

    private val repository: UserRepository = mockk()

    private val service = CheckoutService(
        getUser = GetUserUseCaseImpl(repository),
        validateEmail = ValidateEmailUseCaseImpl(),
        calculateDiscount = CalculateDiscountUseCaseImpl(),
    )

    @Test
    fun `premium user gets 20 percent off through the real use cases`() = runTest {
        coEvery { repository.findById("1") } returns
            User(id = "1", name = "Ada", email = "ada@example.com", isPremium = true)

        val result = service.checkout(userId = "1", cartTotal = 250.0)

        assertEquals(CheckoutResult.Success(200.0), result)
    }

    @Test
    fun `non premium user over threshold gets 5 percent off`() = runTest {
        coEvery { repository.findById("2") } returns
            User(id = "2", name = "Bob", email = "bob@example.com", isPremium = false)

        val result = service.checkout(userId = "2", cartTotal = 200.0)

        assertEquals(CheckoutResult.Success(190.0), result)
    }

    @Test
    fun `user with malformed email is rejected by the real validator`() = runTest {
        coEvery { repository.findById("3") } returns
            User(id = "3", name = "Eve", email = "eve@invalid", isPremium = false)

        val result = service.checkout(userId = "3", cartTotal = 50.0)

        assertEquals(CheckoutResult.Failure("Invalid email"), result)
    }

    @Test
    fun `missing user fails checkout`() = runTest {
        coEvery { repository.findById("ghost") } returns null

        val result = service.checkout(userId = "ghost", cartTotal = 50.0)

        assertEquals(CheckoutResult.Failure("User not found"), result)
    }
}
