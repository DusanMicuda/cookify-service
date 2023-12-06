package com.micudasoftware.api.user

import kotlinx.serialization.Serializable

/**
 * Represents a sign-up request containing the user's information for new account creation.
 *
 * @param name The user's full name.
 * @param email The user's email address.
 * @param password The user's password.
 */
@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
)
