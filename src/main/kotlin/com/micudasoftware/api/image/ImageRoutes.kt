package com.micudasoftware.api.image

import com.micudasoftware.common.SUPPORTED_MIME_TYPES
import com.micudasoftware.data.image.ImageDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
            get {
                val request = call.receive<GetImageRequest>()

                request.validate().onError {
                    call.respond(it.code, it.message)
                    return@get
                }

                imageDataSource.getImage(request.imageUrl)
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
                val request = call.receiveChannel()
                val contentType = call.request.contentType().toString()
                val contentLength = call.request.contentLength()

                when {
                    contentType !in SUPPORTED_MIME_TYPES -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid or missing content type")
                        return@post
                    }

                    contentLength == null -> {
                        call.respond(HttpStatusCode.BadRequest, "Missing Content Length")
                        return@post
                    }

                    contentLength > 15000000L -> {
                        call.respond(HttpStatusCode.Conflict, "Uploaded file is too big. Max size is 15MB")
                        return@post
                    }
                }

                imageDataSource.saveNewImageToCache(request, contentType)
                    .onSuccess { file ->
                        if (file.length() != contentLength) {
                            file.delete()
                            call.respond(HttpStatusCode.InternalServerError, "File size doesn't match content length")
                            return@onSuccess
                        }

                        call.respond(HttpStatusCode.OK, UploadImageResponse(file.path))
                    }.onError {
                        call.respond(it.code, "Image wasn't uploaded")
                    }
            }
        }
    }
}