package com.micudasoftware.data.userprofile

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

/**
 * A data class representing a user profile.
 *
 * @property userId The unique identifier of the user.
 * @property userName The full name of the user.
 * @property aboutMeText The user's description of themselves.
 * @property titlePhoto The name of the user's title photo.
 * @property profilePhoto The name of the user's profile photo.
 */
data class UserProfile(
    @BsonId val userId: ObjectId,
    val userName: String,
    val aboutMeText: String? = null,
    val titlePhoto: String? = null,
    val profilePhoto: String? = null,
)
