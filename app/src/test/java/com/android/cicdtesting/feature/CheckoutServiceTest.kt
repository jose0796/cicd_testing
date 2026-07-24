package com.android.cicdtesting.feature

import com.android.cicdtesting.domain.model.User
import com.android.cicdtesting.domain.usecase.CalculateDiscountUseCase
import com.android.cicdtesting.domain.usecase.GetUserUseCase
import com.android.cicdtesting.domain.usecase.ValidateEmailUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Demonstrates mocking the dummy use cases with MockK and verifying the
 * behaviour of [CheckoutService].
 */
class CheckoutServiceTest {

    private val getUser: GetUserUseCase = mockk()
    private val validateEmail: ValidateEmailUseCase = mockk()
    private val calculateDiscount: CalculateDiscountUseCase = mockk()

    private val service = CheckoutService(getUser, validateEmail, calculateDiscount)

    @Test
    fun `checkout succeeds and applies discount for valid premium user`() = runTest {
        val user = User(id = "1", name = "Ada", email = "ada@example.com", isPremium = true)
        coEvery { getUser("1") } returns user
        every { validateEmail("ada@example.com") } returns true
        every { calculateDiscount(200.0, true) } returns 160.0

        val result = service.checkout(userId = "1", cartTotal = 200.0)

        assertTrue(result is CheckoutResult.Success)
        assertEquals(160.0, (result as CheckoutResult.Success).finalPrice, 0.0001)
        coVerify(exactly = 1) { getUser("1") }
        verify(exactly = 1) { calculateDiscount(200.0, true) }
    }

    @Test
    fun `checkout fails when user is not found`() = runTest {
        coEvery { getUser("missing") } returns null

        val result = service.checkout(userId = "missing", cartTotal = 50.0)

        assertEquals(CheckoutResult.Failure("User not found"), result)
        // Discount must never be attempted if the user does not exist.
        verify(exactly = 0) { calculateDiscount(any(), any()) }
    }

    @Test
    fun `checkout fails when email is invalid`() = runTest {
        val user = User(id = "2", name = "Bob", email = "not-an-email", isPremium = false)
        coEvery { getUser("2") } returns user
        every { validateEmail("not-an-email") } returns false

        val result = service.checkout(userId = "2", cartTotal = 120.0)

        assertEquals(CheckoutResult.Failure("Invalid email"), result)
        verify(exactly = 0) { calculateDiscount(any(), any()) }
    }

    @Test
    fun `checkout passes the user premium flag to the discount calculation`() = runTest {
        val user = User(id = "3", name = "Cleo", email = "cleo@example.com", isPremium = false)
        coEvery { getUser("3") } returns user
        every { validateEmail("cleo@example.com") } returns true
        every { calculateDiscount(80.0, false) } returns 80.0

        service.checkout(userId = "3", cartTotal = 80.0)

        verify(exactly = 1) { calculateDiscount(80.0, false) }
        verify(exactly = 0) { calculateDiscount(any(), true) }
    }

    @Test
    fun `checkout succeeds with a zero cart total`() = runTest {
        val user = User(id = "4", name = "Dan", email = "dan@example.com", isPremium = true)
        coEvery { getUser("4") } returns user
        every { validateEmail("dan@example.com") } returns true
        every { calculateDiscount(0.0, true) } returns 0.0

        val result = service.checkout(userId = "4", cartTotal = 0.0)

        assertEquals(CheckoutResult.Success(0.0), result)
    }

    @Test(expected = IllegalStateException::class)
    fun `checkout propagates unexpected use case errors`() = runTest {
        coEvery { getUser("boom") } throws IllegalStateException("backend down")

        service.checkout(userId = "boom", cartTotal = 10.0)
    }
}
