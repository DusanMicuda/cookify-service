package com.micudasoftware.data.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

/**
 * Data class representing user.
 *
 * @property name Full name of the user.
 * @property email User's email.
 * @property password User's password.
 * @property salt Salt string added to the password
 * @property id User's id.
 */
data class User(
    val name: String,
    val email: String,
    val password: String,
    val salt: String,
    @BsonId val id: ObjectId = ObjectId()
)
