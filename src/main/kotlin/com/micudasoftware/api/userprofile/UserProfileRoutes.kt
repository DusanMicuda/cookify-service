package com.micudasoftware.api.userprofile

import com.micudasoftware.data.userprofile.UserProfile
import com.micudasoftware.data.userprofile.UserProfileDataSource
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import java.io.File
import java.util.*

/**
 * Defines an endpoint for getting and updating user profile.
 * If there are cached images in the request, so they will be moved into users folder, and removed from cache.
 *
 * @param userProfileDataSource The [UserProfileDataSource] instance used to interact with the user profile data storage
 */
fun Route.userProfile(
    userProfileDataSource: UserProfileDataSource
) {
    route("profile") {
        authenticate {
            get {
                val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                if (userId == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Can't get user id from token")
                    return@get
                }

                val userProfile = userProfileDataSource.getUserProfileById(ObjectId(userId))
                if(userProfile == null) {
                    call.respond(HttpStatusCode.NotFound, "User profile with given id wasn't found")
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    GetUserProfileResponse(
                        userId = userProfile.userId.toString(),
                        userName = userProfile.userName,
                        aboutMeText = userProfile.aboutMeText,
                        titlePhotoUrl = userProfile.titlePhoto,
                        profilePhotoUrl = userProfile.profilePhoto
                    )
                )
            }

            post {
                val request = call.receiveNullable<UpdateUserProfileRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                if (userId == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Can't get user id from token")
                    return@post
                }

                var newProfilePhotoUrl: String? = null
                if (request.profilePhotoUrl?.contains("cache") == true) {
                    val file = File("data/${request.profilePhotoUrl}")
                    if (file.exists() && file.canRead()) {
                        val fileName = request.profilePhotoUrl.takeLastWhile { it != '/' }
                        newProfilePhotoUrl = "data/users/$userId/$fileName"
                        file.copyTo(File(newProfilePhotoUrl), true)
                        file.delete()
                    }
                }

                var newTitlePhotoUrl: String? = null
                if (request.titlePhotoUrl?.contains("cache") == true) {
                    val file = File("data/${request.titlePhotoUrl}")
                    if (file.exists() && file.canRead()) {
                        val fileName = request.titlePhotoUrl.takeLastWhile { it != '/' }
                        newTitlePhotoUrl = "data/users/$userId/$fileName"
                        file.copyTo(File(newTitlePhotoUrl), true)
                        file.delete()
                    }
                }

                val wasAcknowledged = userProfileDataSource.updateUserProfile(
                    UserProfile(
                        userId = ObjectId(userId),
                        userName = request.userName,
                        aboutMeText = request.aboutMeText,
                        titlePhoto = newTitlePhotoUrl ?: request.titlePhotoUrl,
                        profilePhoto = newProfilePhotoUrl ?: request.profilePhotoUrl
                    )
                )
                if (!wasAcknowledged) {
                    call.respond(HttpStatusCode.InternalServerError, "User profile wasn't updated")
                    return@post
                }

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

/**
 * Defines an endpoint for getting and uploading images.
 * The images are saved into the caches folder temporally, until their url's will be saved into the database.
 */
fun Route.image() {
    route("image") {
        authenticate {
            post {
                val request = call.receiveMultipart()
                val contentType = call.request.contentType().contentType
                val contentLength = call.request.contentLength()
                var fileUrl = "cache/images/${UUID.randomUUID()}"
                var file: File? = null

                when {
                    !contentType.startsWith("image/") -> {
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

                request.forEachPart { partData ->
                    (partData as? PartData.FileItem)?.let {
                        if (!fileUrl.contains('.')) {
                            fileUrl += partData.originalFileName?.takeLastWhile { it != '.' }
                        }
                        val fileBytes = partData.streamProvider().readBytes()

                        if (file != null) {
                            file?.writeBytes(fileBytes)
                        } else {
                            file = File("data/$fileUrl")
                            file?.writeBytes(fileBytes)
                        }
                    }
                    partData.dispose()
                }

                if (file?.length() != contentLength) {
                    file?.delete()
                    call.respond(HttpStatusCode.InternalServerError, "File size doesn't match content length")
                    return@post
                }

                call.respond(HttpStatusCode.OK, UploadImageResponse(fileUrl))
            }

            get {
                val request = call.receiveNullable<GetImageRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                if (request.imageUrl.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "imageUrl is blank")
                    return@get
                }

                val image = File("data/${request.imageUrl}")
                if (!image.exists() || !image.isFile || !image.canRead()) {
                    call.respond(HttpStatusCode.NotFound, "Image not found")
                    return@get
                }

                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Inline.withParameter(    // Todo If it doesn't work, try to replace inline with attachment
                        ContentDisposition.Parameters.FileName, "data/${request.imageUrl}"
                    ).toString()
                )
                call.respondFile(image)
            }
        }
    }
}