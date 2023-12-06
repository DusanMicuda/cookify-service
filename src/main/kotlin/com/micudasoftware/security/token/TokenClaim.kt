package com.micudasoftware.security.token

/**
 * Represents a single claim within a JSON Web Token (JWT).
 *
 * A claim is a key-value pair that provides additional information about the JWT. For example, a JWT might contain claims about the user who is authorized by the token, such as their username or email address.
 *
 * @param name The name of the claim. This is typically a string that identifies the type of information being conveyed by the claim.
 * @param value The value of the claim. This is the actual information being conveyed by the claim. The type of the value will depend on the specific claim.
 */
data class TokenClaim(
    val name: String,
    val value: String,
)
