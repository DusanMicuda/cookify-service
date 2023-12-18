package com.micudasoftware.api.image

import kotlinx.serialization.Serializable

/**
 * A data class representing a request to get an image.
 *
 * @property imageUrl The unique url where the image is saved .
 */
@Serializable
data class GetImageRequest(
    val imageUrl: String,
)
