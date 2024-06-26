package com.micudasoftware.api.user

import com.micudasoftware.data.user.User
import com.micudasoftware.data.user.UserDataSource
import com.micudasoftware.data.userprofile.UserProfile
import com.micudasoftware.data.userprofile.UserProfileDataSource
import com.micudasoftware.security.hashing.HashingService
import com.micudasoftware.security.hashing.SaltedHash
import com.micudasoftware.security.token.TokenClaim
import com.micudasoftware.security.token.TokenConfig
import com.micudasoftware.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * Defines an endpoint for user sign-up, handling user registration and data validation
 *
 * @param userDataSource The [UserDataSource] instance used to interact with the user data storage
 * @param userProfileDataSource The [UserProfileDataSource] instance used to interact with the user profile data storage
 * @param hashingService The [HashingService] instance used for secure password hashing
 */
fun Route.signUp(
    userDataSource: UserDataSource = inject<UserDataSource>().value,
    userProfileDataSource: UserProfileDataSource = inject<UserProfileDataSource>().value,
    hashingService: HashingService = inject<HashingService>().value
) {
    post("signup") {
        val request = call.receive<SignUpRequest>()

        request.validate().onError {
            call.respond(it.code, it.message)
            return@post
        }

        if (userDataSource.getUserByEmail(request.email) != null) {
            call.respond(HttpStatusCode.Conflict, "Email is already used")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            name = request.name,
            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val userProfile = UserProfile(
            userId = user.id,
            userName = user.name,
        )
        val wasAcknowledged = userDataSource.insertUser(user) && userProfileDataSource.createUserProfile(userProfile)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.InternalServerError, "Data was not saved")
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

/**
 * Defines an endpoint for user login, handling authentication and token generation
 *
 * @param userDataSource The [UserDataSource] instance used to interact with the user data storage
 * @param hashingService The [HashingService] instance used for secure password verification
 * @param tokenService The [TokenService] instance used for generating authentication tokens
 * @param tokenConfig The [TokenConfig] object containing token configuration parameters
 */
fun Route.login(
    userDataSource: UserDataSource = inject<UserDataSource>().value,
    hashingService: HashingService = inject<HashingService>().value,
    tokenService: TokenService = inject<TokenService>().value,
    tokenConfig: TokenConfig = inject<TokenConfig>().value,
) {
    post("login") {
        val request = call.receive<LoginRequest>()

        request.validate().onError {
            call.respond(it.code, it.message)
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "User with the given email does't exist")
            return@post
        }

        val isPasswordValid = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt,
            )
        )
        if (!isPasswordValid) {
            call.respond(HttpStatusCode.Conflict, "Invalid password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(HttpStatusCode.OK, LoginResponse(user.id.toString(), token))
    }
}

/**
 * Defines an endpoint for authentication verification
 */
fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}