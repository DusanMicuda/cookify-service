package com.micudasoftware.api.userprofile

import kotlinx.serialization.Serializable

/**
 * A data class representing a response containing user profile information.
 *
 * @property userId The unique identifier of the user.
 * @property userName The name of the user.
 * @property aboutMeText The user's description of themselves.
 * @property titlePhotoUrl The URL of the user's title photo.
 * @property profilePhotoUrl The URL of the user's profile photo.
 */
@Serializable
data class GetUserProfileResponse(
    val userId: String,
    val userName: String,
    val aboutMeText: String? = null,
    val titlePhotoUrl: String? = null,
    val profilePhotoUrl: String? = null,
)
