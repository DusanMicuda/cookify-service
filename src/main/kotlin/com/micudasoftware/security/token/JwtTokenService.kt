package com.micudasoftware.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

/**
 * Implements the `TokenService` interface for generating JSON Web Tokens (JWTs) using the JWT library.
 */
class JwtTokenService: TokenService {

    override fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String {
        val token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))
        claims.forEach {
            token.withClaim(it.name, it.value)
        }
        return token.sign(Algorithm.HMAC256(config.secret))
    }
}