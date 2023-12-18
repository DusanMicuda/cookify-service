package com.micudasoftware.api.userprofile

import kotlinx.serialization.Serializable

/**
 * A data class representing a response containing the name of a newly uploaded file.
 *
 * @property fileUrl The URL of the newly uploaded image.
 */
@Serializable
data class UploadImageResponse(
    val fileUrl: String
)
