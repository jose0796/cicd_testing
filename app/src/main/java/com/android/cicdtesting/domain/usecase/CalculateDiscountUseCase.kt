package com.android.cicdtesting.domain.usecase

/**
 * Calculates the final price after applying a discount.
 *
 * Premium users get 20% off, everyone else gets 5% off on orders over $100.
 */
fun interface CalculateDiscountUseCase {
    operator fun invoke(price: Double, isPremium: Boolean): Double
}

class CalculateDiscountUseCaseImpl : CalculateDiscountUseCase {
    override fun invoke(price: Double, isPremium: Boolean): Double {
        require(price >= 0) { "price must not be negative" }
        val rate = when {
            isPremium -> 0.20
            price > 100.0 ->
                0.05
            else -> 0.0
        }
        return price * (1 - rate)
    }
}
