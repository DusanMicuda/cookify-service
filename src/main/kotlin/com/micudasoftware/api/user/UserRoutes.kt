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

/**
 * Defines an endpoint for user sign-up, handling user registration and data validation
 *
 * @param userDataSource The [UserDataSource] instance used to interact with the user data storage
 * @param userProfileDataSource The [UserProfileDataSource] instance used to interact with the user profile data storage
 * @param hashingService The [HashingService] instance used for secure password hashing
 */
fun Route.signUp(
    userDataSource: UserDataSource,
    userProfileDataSource: UserProfileDataSource,
    hashingService: HashingService
) {
    post("signup") {
        val request = call.receiveNullable<SignUpRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val fieldsAreBlank = request.name.isBlank() || request.email.isBlank() || request.password.isBlank()
        val emailAlreadyUsed = userDataSource.getUserByEmail(request.email) != null

        when {
            fieldsAreBlank -> {
                call.respond(HttpStatusCode.BadRequest, "Required data are blank")
                return@post
            }
            !isEmailValid(request.email) -> {
                call.respond(HttpStatusCode.BadRequest, "Wrong format of email")
                return@post
            }
            !isPasswordValid(request.password) -> {
                call.respond(HttpStatusCode.BadRequest, "Weak password")
                return@post
            }
            emailAlreadyUsed -> {
                call.respond(HttpStatusCode.Conflict, "Email is already used")
                return@post
            }
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
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
) {
    post("login") {
        val request = call.receiveNullable<LoginRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val fieldsAreBlank = request.email.isBlank() || request.password.isBlank()

        when {
            fieldsAreBlank -> {
                call.respond(HttpStatusCode.BadRequest, "Required data are blank")
                return@post
            }
            !isEmailValid(request.email) -> {
                call.respond(HttpStatusCode.BadRequest, "Wrong format of email")
            }
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
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(HttpStatusCode.OK, LoginResponse(token))
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