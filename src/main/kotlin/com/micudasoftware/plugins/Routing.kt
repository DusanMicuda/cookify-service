package com.micudasoftware.plugins

import com.micudasoftware.api.user.authenticate
import com.micudasoftware.api.user.login
import com.micudasoftware.api.user.signUp
import com.micudasoftware.data.user.UserDataSource
import com.micudasoftware.security.hashing.HashingService
import com.micudasoftware.security.token.TokenConfig
import com.micudasoftware.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signUp(userDataSource, hashingService)
        login(userDataSource, hashingService, tokenService, tokenConfig)
        authenticate()
    }
}
