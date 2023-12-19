package com.micudasoftware.api.image

import com.micudasoftware.common.BaseRequest
import com.micudasoftware.common.Result
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * A data class representing a request to get an image.
 *
 * @property imageUrl The unique url where the image is saved .
 */
@Serializable
data class GetImageRequest(
    val imageUrl: String,
) : BaseRequest {

    override fun validate(): Result<Unit> =
        when {
            imageUrl.isBlank() -> Result.Error(HttpStatusCode.BadRequest, "Image url is blank")
            !imageUrl.startsWith("data/") -> Result.Error(HttpStatusCode.BadRequest, "Invalid Image Url")
            else -> Result.Success(Unit)
        }
}
