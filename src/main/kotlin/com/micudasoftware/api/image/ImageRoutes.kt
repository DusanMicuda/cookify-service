package com.micudasoftware.api.image

import com.micudasoftware.common.Result
import com.micudasoftware.common.SUPPORTED_MIME_TYPES
import com.micudasoftware.data.image.ImageDataSource
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import org.koin.ktor.ext.inject

/**
 * Defines an endpoint for getting and uploading images.
 * The images are saved into the caches folder temporally, until their url's will be saved into the database.
 *
 * @param imageDataSource The data source of the images
 */
fun Route.image(
    imageDataSource: ImageDataSource = inject<ImageDataSource>().value,
) {
    route("image") {
        authenticate {
            get("{path}/{ownerId}/{imageId}") {
                val imageUrl = call.parameters["path"] + "/" + call.parameters["ownerId"] + "/" + call.parameters["imageId"]

                imageDataSource.getImage(imageUrl)
                    .onSuccess {
                        call.response.header(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Inline.withParameter(    // Todo If it doesn't work, try to replace inline with attachment
                                ContentDisposition.Parameters.FileName, it.path
                            ).toString()
                        )
                        call.respondFile(it)
                    }.onError {
                        call.respond(it.code, it.message)
                    }
            }

            post {
                val request = call.receiveMultipart()

                var result: Result<ByteReadChannel>? = null
                var fileName = ""
                var fileSize = 0L
                request.forEachPart { partData ->
                    when (partData) {
                        is PartData.FileItem -> {
                            val fileBytes = partData.streamProvider().toByteReadChannel()
                            val contentType = partData.contentType.toString()
                            fileSize = fileBytes.totalBytesRead
                            when {
                                contentType !in SUPPORTED_MIME_TYPES -> {
                                    result = Result.Error(HttpStatusCode.BadRequest, "Invalid or missing content type")
                                    return@forEachPart
                                }

                                fileSize > 15000000L -> {
                                    result = Result.Error(HttpStatusCode.Conflict, "Uploaded file is too big. Max size is 15MB")
                                    return@forEachPart
                                }
                            }
                            fileName = partData.originalFileName ?: ""
                            result = Result.Success(fileBytes)
                            partData.dispose()
                        }

                        else -> {}
                    }
                }

                result?.onSuccess { imageBytes ->
                    imageDataSource.saveNewImageToCache(imageBytes, fileName)
                        .onSuccess { file ->
                            if (file.length() != fileSize) {
                                file.delete()
                                call.respond(HttpStatusCode.InternalServerError, "File size doesn't match content length")
                                return@post
                            }

                            call.respond(HttpStatusCode.OK, UploadImageResponse(file.path.removePrefix("data/")))
                        }.onError {
                            call.respond(it.code, "Image wasn't uploaded")
                        }
                }
            }
        }
    }
}