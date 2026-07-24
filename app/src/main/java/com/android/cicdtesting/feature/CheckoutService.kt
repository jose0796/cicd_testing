package com.android.cicdtesting.feature

import com.android.cicdtesting.domain.usecase.CalculateDiscountUseCase
import com.android.cicdtesting.domain.usecase.GetUserUseCase
import com.android.cicdtesting.domain.usecase.ValidateEmailUseCase

/**
 * Result of a checkout attempt.
 */
sealed interface CheckoutResult {
    data class Success(val finalPrice: Double) : CheckoutResult
    data class Failure(val reason: String) : CheckoutResult
}

/**
 * Orchestrates the dummy use cases. Because every dependency is an interface,
 * this class is trivial to unit test by mocking each use case.
 */
class CheckoutService(
    private val getUser: GetUserUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val calculateDiscount: CalculateDiscountUseCase,
) {
    suspend fun checkout(userId: String, cartTotal: Double): CheckoutResult {
        val user = getUser(userId)
            ?: return CheckoutResult.Failure("User not found")

        if (!validateEmail(user.email)) {
            return CheckoutResult.Failure("Invalid email")
        }

        val finalPrice = calculateDiscount(cartTotal, user.isPremium)
        return CheckoutResult.Success(finalPrice)
    }
}
