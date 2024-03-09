package com.micudasoftware.api.recipe

import kotlinx.serialization.Serializable

/**
 * Data class represents author of the recipe.
 *
 * @property id The user's id.
 * @property name The user's name.
 * @property profilePhotoUrl The URL of the user's profile photo.
 */
@Serializable
data class Author(
    val id: String,
    val name: String,
    val profilePhotoUrl: String?,
)
