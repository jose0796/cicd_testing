package com.android.cicdtesting.domain.usecase

/**
 * Validates an email address. Pure function -> easy to unit test and to mock.
 */
fun interface ValidateEmailUseCase {
    operator fun invoke(email: String): Boolean
}

class ValidateEmailUseCaseImpl : ValidateEmailUseCase {
    private val pattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override fun invoke(email: String): Boolean =
        email.isNotBlank() && pattern.matches(email)
}
