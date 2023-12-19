package com.micudasoftware.data.image

import com.micudasoftware.common.Result
import io.ktor.http.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

/**
 * The instance of [ImageDataSource] to save images locally.
 */
class LocalImageDataSource : ImageDataSource {

    override suspend fun getImage(url: String): Result<File> {
        val image = File(url)
        return if (image.exists() && image.isFile && image.canRead()) {
            Result.Success(image)
        } else {
            Result.Error(
                HttpStatusCode.NotFound,
                "Image wasn't found"
            )
        }
    }

    override suspend fun saveNewImageToCache(
        channel: ByteReadChannel,
        mimeType: String
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val dir = File("data/cache/images")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val extension = convertMimeToExtension(mimeType)
            val newFile = File("data/cache/images/${UUID.randomUUID()}$extension")
            newFile.createNewFile()

            channel.copyAndClose(newFile.writeChannel())
            return@withContext Result.Success(newFile)
        } catch (e: Exception) {
            return@withContext Result.Error(message = e.message ?: "Unknown error")
        }
    }

    override suspend fun saveImageFromCache(imageUrl: String, newPath: String): Result<NewImageUrl> =
        withContext(Dispatchers.IO) {
            if (imageUrl.contains("cache")) {
                val file = File(imageUrl)
                if (file.exists() && file.canRead()) {
                    val fileName = imageUrl.takeLastWhile { it != '/' }
                    val newFile = File("data/$newPath/$fileName")
                    file.copyTo(newFile, true)
                    file.delete()
                    Result.Success(newFile.path as NewImageUrl)
                } else {
                    Result.Error(
                        HttpStatusCode.InternalServerError,
                        "Image doesn't exist, or isn't accessible"
                    )
                }
            } else {
                Result.Success(imageUrl)
            }
        }

    /**
     * Converts a MIME type to a file extension.
     *
     * @param mimeType The MIME type to convert.
     * @return A [String] representing the file extension.
     */
    private fun convertMimeToExtension(mimeType: String): String {
        return when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            "image/gif" -> ".gif"
            "image/webp" -> ".webp"
            else -> ""
        }
    }
}