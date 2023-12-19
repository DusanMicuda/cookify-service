package com.micudasoftware.api.userprofile

import com.micudasoftware.common.BaseRequest
import com.micudasoftware.common.Result
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Data class representing request to get user profile data.
 *
 * @property userId The unique identifier of the user.
 */
@Serializable
data class GetUserProfileRequest(
    val userId: String,
) : BaseRequest {

    override fun validate(): Result<Unit> =
        when {
            userId.isBlank() -> Result.Error(HttpStatusCode.BadRequest, "User id is blank")
            else -> Result.Success(Unit)
        }
}
