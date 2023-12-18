package com.micudasoftware.api.image

import com.micudasoftware.common.SUPPORTED_MIME_TYPES
import com.micudasoftware.data.image.ImageDataSource
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Defines an endpoint for getting and uploading images.
 * The images are saved into the caches folder temporally, until their url's will be saved into the database.
 *
 * @param imageDataSource The data source of the images
 */
fun Route.image(
    imageDataSource: ImageDataSource
) {
    route("image") {
        authenticate {
            get {
                val request = call.receiveNullable<GetImageRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                if (request.imageUrl.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Image url is blank")
                    return@get
                }

                if (!request.imageUrl.startsWith("data/")) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Image Url")
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
                    }.onError { code, message ->
                        call.respond(code, message)
                    }
            }

            post {
                val request = call.receiveMultipart()
                val contentType = call.request.contentType().contentType
                val contentLength = call.request.contentLength()

                when {
                    contentType in SUPPORTED_MIME_TYPES -> {
                        call.respond(HttpStatusCode.BadRequest, "Invalid or missing content type")
                        return@post
                    }

                    contentLength == null -> {
                        call.respond(HttpStatusCode.BadRequest, "Missing Content Length")
                        return@post
                    }

                    contentLength > 15000000L -> {
                        call.respond(HttpStatusCode.Conflict, "Uploaded file is too big. Max size is 15MB")
                    }
                }

                val file = imageDataSource.getNewCachedImage(contentType)
                request.forEachPart { partData ->
                    (partData as? PartData.FileItem)?.let { item ->
                        val fileBytes = item.streamProvider().readBytes()
                        file.writeBytes(fileBytes)
                    }
                    partData.dispose()
                }

                if (file.length() != contentLength) {
                    file.delete()
                    call.respond(HttpStatusCode.InternalServerError, "File size doesn't match content length")
                    return@post
                }

                call.respond(HttpStatusCode.OK, UploadImageResponse(file.path))
            }
        }
    }
}