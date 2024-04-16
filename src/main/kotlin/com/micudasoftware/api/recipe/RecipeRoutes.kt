package com.micudasoftware.api.recipe

import com.micudasoftware.data.image.ImageDataSource
import com.micudasoftware.data.recipe.Rating
import com.micudasoftware.data.recipe.Recipe
import com.micudasoftware.data.recipe.RecipeDataSource
import com.micudasoftware.data.userprofile.UserProfileDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject

/**
 * Defines an endpoints to create, update and get [Recipe].
 *
 * @param recipeDataSource Data source for recipes.
 * @param imageDataSource Data source for images.
 * @param userProfileDataSource Data source for user profiles.
 */
fun Route.recipe(
    recipeDataSource: RecipeDataSource = inject<RecipeDataSource>().value,
    imageDataSource: ImageDataSource = inject<ImageDataSource>().value,
    userProfileDataSource: UserProfileDataSource = inject<UserProfileDataSource>().value,
) {
    route("recipe") {
        authenticate {
            post {
                val request = call.receive<CreateUpdateRecipeRequest>()

                request.validate().onError {
                    call.respond(it.code, it.message)
                    return@post
                }

                val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                if (userId == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Can't get userId")
                    return@post
                }

                val newRecipeId = ObjectId()

                val photos = request.photos.mapNotNull {
                    imageDataSource.saveImageFromCache(
                        imageUrl = it,
                        newPath = "recipes/${newRecipeId}"
                    ).getOrNull()
                }

                val wasAcknowledged = recipeDataSource.createRecipe(
                    Recipe(
                        id = newRecipeId,
                        authorId = ObjectId(userId),
                        name = request.name,
                        ingredients = request.ingredients,
                        preparation = request.preparation,
                        photos = photos
                    )
                )
                if (wasAcknowledged) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Recipe wasn't saved")
                }
            }

            get("{recipeId}") {
                val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                if (userId == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Can't get userId")
                    return@get
                }

                val recipeId = call.parameters["recipeId"]
                if (recipeId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Recipe id is blank")
                    return@get
                }

                val recipe = recipeDataSource.getRecipeById(ObjectId(recipeId))
                if (recipe == null) {
                    call.respond(HttpStatusCode.NotFound, "Recipe with given id wasn't found")
                    return@get
                }

                val author = userProfileDataSource.getUserProfileById(recipe.authorId)

                val recipeResponse = GetRecipeResponse(
                    id = recipe.id.toString(),
                    timestamp = recipe.timestamp,
                    author = author?.let {
                        Author(
                            id = author.userId.toString(),
                            name = author.userName,
                            profilePhotoUrl = author.profilePhoto
                        )
                    },
                    name = recipe.name,
                    ingredients = recipe.ingredients,
                    preparation = recipe.preparation,
                    rating = recipe.ratings.sumOf { it.rating } / recipe.ratings.size,
                    myRating = recipe.ratings.find { it.userId == userId }?.rating,
                    ratingCount = recipe.ratings.size,
                    photos = recipe.photos
                )
                call.respond(HttpStatusCode.OK, recipeResponse)
            }

            put("{recipeId}") {
                val request = call.receive<CreateUpdateRecipeRequest>()
                val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
                if (userId == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Can't get userId")
                    return@put
                }

                val recipeId = call.parameters["recipeId"]
                if (recipeId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Recipe id is blank")
                    return@put
                }

                request.validate().onError {
                    call.respond(it.code, it.message)
                    return@put
                }

                val recipe = recipeDataSource.getRecipeById(ObjectId(recipeId))
                if (recipe == null) {
                    call.respond(HttpStatusCode.NotFound, "Recipe with given id wasn't found")
                    return@put
                }

                if (recipe.authorId.toString() != userId) {
                    call.respond(HttpStatusCode.Conflict, "Cannot update recipe that is not yours")
                    return@put
                }

                val wasAcknowledged = recipeDataSource.updateRecipe(
                    Recipe(
                        id = recipe.id,
                        timestamp = recipe.timestamp,
                        authorId = recipe.authorId,
                        name = request.name,
                        ingredients = request.ingredients,
                        preparation = request.preparation,
                        photos = request.photos,
                    )
                )

                if (wasAcknowledged) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Recipe wasn't updated")
                }
            }
        }
    }
}

/**
 * Defines an endpoint to get latest recipes.
 *
 * Contains query parameters:
 * - `count` Number of recipes to get.
 * - `offset` Number of recipes to skip.
 * - `regex` String representing regular expression to filter by names.
 *
 * @param recipeDataSource Data source for recipes.
 * @param userProfileDataSource Data source for user profiles.
 */
fun Route.latestRecipes(
    recipeDataSource: RecipeDataSource = inject<RecipeDataSource>().value,
    userProfileDataSource: UserProfileDataSource = inject<UserProfileDataSource>().value,
) {
    authenticate {
        get("latestRecipes") {
            val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
            if (userId == null) {
                call.respond(HttpStatusCode.InternalServerError, "Can't get userId")
                return@get
            }

            val count = call.request.queryParameters["count"]?.toIntOrNull() ?: 10
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val regex = call.request.queryParameters["regex"]
            val recipes = recipeDataSource.getLatestRecipes(count, offset, regex)

            recipes.map { recipe ->
                val author = userProfileDataSource.getUserProfileById(recipe.authorId)

                GetRecipeResponse(
                    id = recipe.id.toString(),
                    timestamp = recipe.timestamp,
                    author = author?.let {
                        Author(
                            id = author.userId.toString(),
                            name = author.userName,
                            profilePhotoUrl = author.profilePhoto
                        )
                    },
                    name = recipe.name,
                    ingredients = recipe.ingredients,
                    preparation = recipe.preparation,
                    rating = recipe.ratings.sumOf { it.rating } / recipe.ratings.size,
                    myRating = recipe.ratings.find { it.userId == userId }?.rating,
                    ratingCount = recipe.ratings.size,
                    photos = recipe.photos
                )
            }

            call.respond(HttpStatusCode.OK, recipes)
        }
    }
}

fun Route.rateRecipe(
    recipeDataSource: RecipeDataSource = inject<RecipeDataSource>().value,
) {
    authenticate {
        put("rating") {
            val request = call.receive<RateRecipeRequest>()

            request.validate().onError {
                call.respond(it.code, it.message)
                return@put
            }

            val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
            if (userId == null) {
                call.respond(HttpStatusCode.InternalServerError, "Can't get userId")
                return@put
            }

            val recipe = recipeDataSource.getRecipeById(ObjectId(request.recipeId))
            if (recipe == null) {
                call.respond(HttpStatusCode.NotFound, "Recipe with given id doesn't exits.")
                return@put
            }

            val wasAcknowledged = recipeDataSource.updateRecipe(
                recipe.copy(
                    ratings = recipe.ratings.toMutableList()
                        .apply {
                            add(
                                Rating(
                                    userId = userId,
                                    rating = request.rating
                                )
                            )
                        }
                )
            )

            if (wasAcknowledged) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Recipe wasn't rated")
            }
        }
    }
}