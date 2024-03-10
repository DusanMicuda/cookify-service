package com.micudasoftware.data.recipe

import org.bson.types.ObjectId

/**
 * The data source for recipes.
 */
interface RecipeDataSource {

    /**
     * Function to create recipe.
     *
     * @param recipe The [Recipe] to create.
     * @return Boolean that indicates if recipe was created or not.
     */
    suspend fun createRecipe(recipe: Recipe): Boolean

    /**
     * Function to update recipe.
     *
     * @param recipe The recipe with existing id and data to update.
     * @return Boolean that indicates if recipe was updated or not.
     */
    suspend fun updateRecipe(recipe: Recipe): Boolean

    /**
     * Function to get recipe by its id.
     *
     * @param recipeId The id of recipe.
     * @return The [Recipe] if found.
     */
    suspend fun getRecipeById(recipeId: ObjectId): Recipe?

    /**
     * Function to get latest recipes.
     *
     * @param count The number of recipes to get.
     * @param offset The number of recipes to skip.
     * @return The [List] of [Recipe].
     */
    suspend fun getLatestRecipes(count: Int, offset: Int): List<Recipe>
}