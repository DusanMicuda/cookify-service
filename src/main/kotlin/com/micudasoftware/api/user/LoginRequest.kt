package com.micudasoftware.api.user

import kotlinx.serialization.Serializable

/**
 * Represents a login request containing the user's credentials.
 *
 * @param email The user's email address.
 * @param password The user's password.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)
