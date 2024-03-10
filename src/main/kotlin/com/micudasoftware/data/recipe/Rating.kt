package com.micudasoftware.data.recipe

import kotlinx.serialization.Serializable

/**
 * Data class representing recipe rating.
 *
 * @property userId The id of user that provided the rating.
 * @property rating The actual rating.
 */
@Serializable
data class Rating(
    val userId: String,
    val rating: Double,
)
