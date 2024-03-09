package com.micudasoftware.data.recipe

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

/**
 * Data class representing Recipe.
 *
 * @property id The id of Recipe.
 * @property timestamp The timestamp of creation.
 * @property authorId The id of author.
 * @property name The recipe name.
 * @property ingredients The ingredients represented as [List] of [Ingredient].
 * @property preparation The preparation guide.
 * @property rating The rating of the recipe.
 * @property ratingCount The number of ratings.
 * @property photos The photos.
 */
data class Recipe(
    @BsonId val id: ObjectId = ObjectId(),
    val timestamp: Long = System.currentTimeMillis(),
    val authorId: ObjectId,
    val name: String,
    val ingredients: List<Ingredient>,
    val preparation: String,
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val photos: List<String>,
)
