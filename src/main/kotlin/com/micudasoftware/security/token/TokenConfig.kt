package com.micudasoftware.security.token

/**
 * Represents the configuration for generating and verifying JSON Web Tokens (JWTs).
 *
 * @param issuer The issuer of the JWT token. This is typically the identifier of the application or service issuing the token.
 * @param audience The audience for the JWT token. This is typically the identifier of the application or service intended to receive the token.
 * @param expiresIn The expiration time for the JWT token. This is the time in milliseconds after which the token will no longer be valid.
 * @param secret The secret key used to sign and verify the JWT token. This key should be kept confidential to prevent unauthorized access to the token.
 */
data class TokenConfig(
    val issuer: String,
    val audience: String,
    val expiresIn: Long,
    val secret: String,
)
