package com.micudasoftware.data.recipe

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

/**
 * The implementation of [RecipeDataSource] using Mongo database.
 *
 * @param db The Mongo database instance.
 * @see RecipeDataSource
 */
class MongoRecipeDatasource(
    db: CoroutineDatabase
): RecipeDataSource {

    private val recipes = db.getCollection<Recipe>()

    override suspend fun createRecipe(recipe: Recipe): Boolean =
        recipes.insertOne(recipe).wasAcknowledged()

    override suspend fun updateRecipe(recipe: Recipe): Boolean =
        recipes.updateOneById(recipe.id, recipe).wasAcknowledged()

    override suspend fun getRecipeById(recipeId: ObjectId): Recipe? =
        recipes.findOne(Recipe::id eq recipeId)

    override suspend fun getLatestRecipes(count: Int, offset: Int, regex: String?): List<Recipe> =
        recipes
            .find(/*regex?.let { Recipe::name regex regex }*/)
            .descendingSort(Recipe::timestamp)
            .skip(offset)
            .limit(count)
            .toList()
}