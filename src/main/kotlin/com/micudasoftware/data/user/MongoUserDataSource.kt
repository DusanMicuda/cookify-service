package com.micudasoftware.data.user

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

/**
 * Data source of users.
 *
 * @param db Mongo database.
 */
class MongoUserDataSource(
    db: CoroutineDatabase
): UserDataSource{

    val users = db.getCollection<User>()

    override suspend fun getUserByEmail(email: String): User? =
        users.findOne(User::email eq email)

    override suspend fun insertUser(user: User): Boolean =
        users.insertOne(user).wasAcknowledged()
}