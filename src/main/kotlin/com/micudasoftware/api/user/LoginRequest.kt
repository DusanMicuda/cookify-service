package com.micudasoftware.api.user

import com.micudasoftware.common.BaseRequest
import com.micudasoftware.common.Result
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Represents a login request containing the user's credentials.
 *
 * @param email The user's email address.
 * @param password The user's password.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
) : BaseRequest {

    override fun validate(): Result<Unit> =
        when {
            email.isBlank() || password.isBlank() ->
                Result.Error(HttpStatusCode.BadRequest, "Required data are blank")

            !isEmailValid(email) ->
                Result.Error(HttpStatusCode.BadRequest, "Wrong format of email")

            else -> Result.Success(Unit)
        }
}
