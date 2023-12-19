package com.micudasoftware.data.image

import com.micudasoftware.common.Result
import io.ktor.utils.io.*
import java.io.File

/**
 * Data source for the images.
 */
interface ImageDataSource {

    /**
     * Fetches an image from a given URL.
     *
     * @param url The URL of the image to fetch.
     * @return A [Result] object containing a [File] if the operation is successful, or an error otherwise.
     */
    suspend fun getImage(url: String): Result<File>

    /**
     * Creates a new cached image file.
     *
     * @param channel The [ByteReadChannel] that contains the uploaded image.
     * @param mimeType The MIME type of the image to be cached.
     * @return If succeeds, [Result] with [NewImageUrl] representing the URL of newly created cached image,
     * otherwise an error.
     */
    suspend fun saveNewImageToCache(
        channel: ByteReadChannel,
        mimeType: String
    ): Result<File>

    /**
     * Saves an image from cache to a new path.
     *
     * @param imageUrl The URL of the image in the cache.
     * @param newPath The new path where the image should be saved.
     * @return A [Result] object containing a new image URL ([NewImageUrl]) if the operation is successful, or an error otherwise.
     */
    suspend fun saveImageFromCache(
        imageUrl: String,
        newPath: String,
    ): Result<NewImageUrl>
}

/**
 * [NewImageUrl] is a typealias for `String` used to represent new image URLs.
 */
typealias NewImageUrl = String