package com.micudasoftware.api.user

import kotlinx.serialization.Serializable

/**
 * Represents a login response containing the authentication token for the logged-in user.
 *
 * @param token The generated authentication token for the user.
 */
@Serializable
data class LoginResponse(
    val token: String
)
