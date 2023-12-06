package com.micudasoftware.security.token

/**
 * Defines an interface for generating tokens.
 */
interface TokenService {

    /**
     * Generates a new token based on the provided configuration and claims.
     *
     * @param config The configuration for generating the JWT, including the issuer, audience, expiration time, and secret key.
     * @param claims The claims to include in the token, represented as an array of [TokenClaim] objects.
     * @return The generated token as a string.
     */
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String
}