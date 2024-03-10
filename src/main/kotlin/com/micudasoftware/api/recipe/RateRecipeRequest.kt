package com.micudasoftware.api.recipe

import com.micudasoftware.common.BaseRequest
import com.micudasoftware.common.Result
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Data class representing request to rate recipe.
 *
 * @property recipeId The id of recipe.
 * @property rating The actual rating.
 */
@Serializable
data class RateRecipeRequest(
    val recipeId: String,
    val rating: Double,
) : BaseRequest {

    override fun validate(): Result<Unit> =
        when {
            recipeId.isBlank() -> Result.Error(HttpStatusCode.BadRequest, "Wrong recipe id!")
            rating > 5.0 -> Result.Error(HttpStatusCode.BadRequest, "Rating cannot be greater than 5!")
            rating < 1.0 -> Result.Error(HttpStatusCode.BadRequest, "Rating cannot be less than 1!")
            else -> Result.Success(Unit)
        }
}
