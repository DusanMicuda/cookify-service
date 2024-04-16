package com.micudasoftware.api.recipe

import com.micudasoftware.common.BaseRequest
import com.micudasoftware.common.Result
import com.micudasoftware.data.recipe.Ingredient
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Data class representing the request to create or update new recipe.
 *
 * @property name The name of the recipe.
 * @property ingredients The ingredients represented as [List] of [Ingredient].
 * @property preparation The preparation guide.
 * @property photos The photos represented as [List] of URLs.
 */
@Serializable
data class CreateUpdateRecipeRequest(
    val name: String,
    val ingredients: List<Ingredient>,
    val preparation: String,
    val photos: List<String>,
) : BaseRequest {

    override fun validate(): Result<Unit> =
        when {
            name.isBlank() -> Result.Error(HttpStatusCode.BadRequest, "Recipe name is blank")
            ingredients.isEmpty() -> Result.Error(HttpStatusCode.BadRequest, "Ingredients are empty")
            ingredients.hasBlankFields() -> Result.Error(HttpStatusCode.BadRequest, "Ingredient has blank field")
            preparation.isBlank() -> Result.Error(HttpStatusCode.BadRequest, "Preparation is blank")
            photos.isEmpty() -> Result.Error(HttpStatusCode.BadRequest, "Photos are empty, upload at least one photo")
            photos.any { it.isBlank() } -> Result.Error(HttpStatusCode.BadRequest, "Photo url is blank")
            photos.size > 5 -> Result.Error(HttpStatusCode.BadRequest, "Too many photos, max is 5")
            else -> Result.Success(Unit)
        }

    private fun List<Ingredient>.hasBlankFields() =
        this.any { it.name.isBlank() || it.quantity.isBlank() }
}
