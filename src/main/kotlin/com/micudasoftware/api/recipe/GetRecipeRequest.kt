package com.micudasoftware.api.recipe

import com.micudasoftware.common.BaseRequest
import com.micudasoftware.common.Result
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Data class representing request to get Recipe.
 *
 * @property recipeId The id of the requested recipe.
 */
@Serializable
data class GetRecipeRequest(
    val recipeId: String,
) : BaseRequest {

    override fun validate(): Result<Unit> =
        when {
            recipeId.isBlank() -> Result.Error(HttpStatusCode.BadRequest, "Recipe id is blank")
            else -> Result.Success(Unit)
        }
}
