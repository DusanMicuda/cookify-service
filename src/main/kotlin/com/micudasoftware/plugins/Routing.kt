package com.micudasoftware.plugins

import com.micudasoftware.api.image.image
import com.micudasoftware.api.user.authenticate
import com.micudasoftware.api.user.login
import com.micudasoftware.api.user.signUp
import com.micudasoftware.api.userprofile.userProfile
import com.micudasoftware.data.image.ImageDataSource
import com.micudasoftware.data.user.UserDataSource
import com.micudasoftware.data.userprofile.UserProfileDataSource
import com.micudasoftware.security.hashing.HashingService
import com.micudasoftware.security.token.TokenConfig
import com.micudasoftware.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    userProfileDataSource: UserProfileDataSource,
    imageDataSource: ImageDataSource,
) {
    routing {
        signUp(userDataSource,userProfileDataSource, hashingService)
        login(userDataSource, hashingService, tokenService, tokenConfig)
        authenticate()

        userProfile(
            userProfileDataSource = userProfileDataSource,
            imageDataSource = imageDataSource
        )
        image(imageDataSource = imageDataSource)
    }
}
