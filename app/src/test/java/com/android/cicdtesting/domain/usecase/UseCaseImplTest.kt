package com.android.cicdtesting.domain.usecase

import com.android.cicdtesting.domain.model.User
import com.android.cicdtesting.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateEmailUseCaseImplTest {
    private val validate = ValidateEmailUseCaseImpl()

    @Test
    fun `accepts a well formed email`() {
        assertTrue(validate("ada@example.com"))
    }

    @Test
    fun `rejects a malformed email`() {
        assertFalse(validate("nope"))
        assertFalse(validate(""))
    }
}

class CalculateDiscountUseCaseImplTest {
    private val calculate = CalculateDiscountUseCaseImpl()

    @Test
    fun `premium users get 20 percent off`() {
        assertEquals(80.0, calculate(100.0, true), 0.0001)
    }

    @Test
    fun `non premium orders over 100 get 5 percent off`() {
        assertEquals(190.0, calculate(200.0, false), 0.0001)
    }

    @Test
    fun `small non premium orders get no discount`() {
        assertEquals(50.0, calculate(50.0, false), 0.0001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative price throws`() {
        calculate(-1.0, false)
    }
}

class GetUserUseCaseImplTest {

    private val repository: UserRepository = mockk()
    private val getUser = GetUserUseCaseImpl(repository)

    @Test
    fun `delegates to the repository`() = runTest {
        val user = User(id = "1", name = "Ada", email = "ada@example.com")
        coEvery { repository.findById("1") } returns user

        val result = getUser("1")

        assertEquals(user, result)
        coVerify(exactly = 1) { repository.findById("1") }
    }

    @Test
    fun `returns null when repository has no user`() = runTest {
        coEvery { repository.findById("x") } returns null
        assertNull(getUser("x"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank id throws`() = runTest {
        getUser("  ")
    }
}
