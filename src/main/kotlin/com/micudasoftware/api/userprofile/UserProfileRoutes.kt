package com.micudasoftware.api.userprofile

import com.micudasoftware.data.image.ImageDataSource
import com.micudasoftware.data.userprofile.UserProfile
import com.micudasoftware.data.userprofile.UserProfileDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

/**
 * Defines an endpoint for getting and updating user profile.
 * If there are cached images in the request, so they will be moved into users folder, and removed from cache.
 *
 * @param userProfileDataSource The [UserProfileDataSource] instance used to interact with the user profile data storage
 */
fun Route.userProfile(
    userProfileDataSource: UserProfileDataSource,
    imageDataSource: ImageDataSource,
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

                val newProfilePhotoUrl = request.profilePhotoUrl?.let { url ->
                    imageDataSource.saveImageFromCache(
                        imageUrl = url,
                        newPath = "users/$userId"
                    ).getOrNull()
                }

                val newTitlePhotoUrl = request.titlePhotoUrl?.let { url ->
                    imageDataSource.saveImageFromCache(
                        imageUrl = url,
                        newPath = "users/$userId"
                    ).getOrNull()
                }

                val wasAcknowledged = userProfileDataSource.updateUserProfile(
                    UserProfile(
                        userId = ObjectId(userId),
                        userName = request.userName,
                        aboutMeText = request.aboutMeText,
                        profilePhoto = newProfilePhotoUrl ?: request.profilePhotoUrl,
                        titlePhoto = newTitlePhotoUrl ?: request.titlePhotoUrl
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
