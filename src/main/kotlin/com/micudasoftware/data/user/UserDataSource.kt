package com.micudasoftware.data.user

/**
 * Data source of the users.
 */
interface UserDataSource {

    /**
     * Function to get user by email from the database
     *
     * @param email User's email.
     * @return [User] if the user was found, otherwise null.
     */
    suspend fun getUserByEmail(email: String): User?

    /**
     * Function to create new user.
     *
     * @param user [User] to be created.
     * @return Flag that indicates if user was created or not.
     */
    suspend fun insertUser(user: User): Boolean
}