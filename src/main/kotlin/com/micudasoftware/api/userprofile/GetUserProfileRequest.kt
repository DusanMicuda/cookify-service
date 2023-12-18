package com.micudasoftware.api.userprofile

import kotlinx.serialization.Serializable

/**
 * Data class representing request to get user profile data.
 *
 * @property userId The unique identifier of the user.
 */
@Serializable
data class GetUserProfileRequest(
    val userId: String,
)
