package com.micudasoftware.api.userprofile

import com.micudasoftware.common.BaseRequest
import com.micudasoftware.common.Result
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * A data class representing a request to update a user profile.
 *
 * @property userName The full name of the user.
 * @property aboutMeText The user's description of themselves.
 * @property titlePhotoUrl The URL of the user's title photo.
 * @property profilePhotoUrl The URL of the user's profile photo.
 */

@Serializable
data class UpdateUserProfileRequest(
    val userName: String,
    val aboutMeText: String? = null,
    val titlePhotoUrl: String? = null,
    val profilePhotoUrl: String? = null,
) : BaseRequest {

    override fun validate(): Result<Unit> =
        when {
            userName.isBlank() ||
                aboutMeText?.isBlank() == true ||
                titlePhotoUrl?.isBlank() == true ||
                profilePhotoUrl?.isBlank() == true ->
                    Result.Error(HttpStatusCode.BadRequest, "Required fields are blank")

            titlePhotoUrl?.startsWith("data/") == false ||
                profilePhotoUrl?.startsWith("data/") == false ->
                    Result.Error(HttpStatusCode.BadRequest, "Wrong image URL")

            else -> Result.Success(Unit)
        }
}
