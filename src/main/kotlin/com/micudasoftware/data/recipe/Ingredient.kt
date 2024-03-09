package com.micudasoftware.data.recipe

import kotlinx.serialization.Serializable

/**
 * Data class representing ingredient.
 *
 * @property name The name of the ingredient.
 * @property quantity The required quantity.
 */
@Serializable
data class Ingredient(
    val name: String,
    val quantity: String,
)
