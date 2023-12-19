package com.micudasoftware.api.user

import com.micudasoftware.common.BaseRequest
import com.micudasoftware.common.Result
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Represents a sign-up request containing the user's information for new account creation.
 *
 * @param name The user's full name.
 * @param email The user's email address.
 * @param password The user's password.
 */
@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
) : BaseRequest {

    override fun validate(): Result<Unit> =
        when {
            name.isBlank() || email.isBlank() || password.isBlank() ->
                Result.Error(HttpStatusCode.BadRequest, "Required data are blank")

            !isEmailValid(email) ->
                Result.Error(HttpStatusCode.BadRequest, "Wrong format of email")

            !isPasswordValid(password) ->
                Result.Error(HttpStatusCode.BadRequest, "Weak password")

            else -> Result.Success(Unit)
        }
}
