package com.android.cicdtesting.domain.model

/**
 * Simple domain model used by the dummy use cases.
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val isPremium: Boolean = false,
)
