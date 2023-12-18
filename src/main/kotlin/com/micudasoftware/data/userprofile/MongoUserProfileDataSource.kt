package com.micudasoftware.data.userprofile

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

/**
 * A class representing a data source for user profiles stored in a MongoDB database.
 *
 * @property db The database instance.
 */
class MongoUserProfileDataSource(
    db: CoroutineDatabase
): UserProfileDataSource {

    private val userProfiles = db.getCollection<UserProfile>()

    override suspend fun getUserProfileById(userId: ObjectId) =
        userProfiles.findOne(UserProfile::userId eq userId)


    override suspend fun createUserProfile(userProfile: UserProfile): Boolean =
        userProfiles.insertOne(userProfile).wasAcknowledged()

    override suspend fun updateUserProfile(userProfile: UserProfile): Boolean =
        userProfiles.updateOne(UserProfile::userId eq userProfile.userId, userProfile).wasAcknowledged()
}