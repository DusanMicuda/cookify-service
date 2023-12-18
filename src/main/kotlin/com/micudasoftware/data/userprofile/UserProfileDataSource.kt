package com.micudasoftware.data.userprofile

import org.bson.types.ObjectId

/**
 * Data source of the user profiles.
 */
interface UserProfileDataSource {

    /**
     * Function to get user profile by id from the database
     *
     * @param userId Id of the user
     * @return [UserProfile] if found, otherwise null
     */
    suspend fun getUserProfileById(userId: ObjectId): UserProfile?

    /**
     * Function to create new user profile
     *
     * @param userProfile [UserProfile] to be created
     * @return True, if was created, otherwise false
     */
    suspend fun createUserProfile(userProfile: UserProfile): Boolean

    /**
     * Function to update existing user profile
     *
     * @param userProfile [UserProfile] to be updated. I must contain an existing userId
     * @return True, if was updated, otherwise false
     */
    suspend fun updateUserProfile(userProfile: UserProfile): Boolean
}