package com.micudasoftware.data.di

import com.micudasoftware.data.image.ImageDataSource
import com.micudasoftware.data.image.LocalImageDataSource
import com.micudasoftware.data.user.MongoUserDataSource
import com.micudasoftware.data.user.UserDataSource
import com.micudasoftware.data.userprofile.MongoUserProfileDataSource
import com.micudasoftware.data.userprofile.UserProfileDataSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val dataModule = module {
    single<CoroutineDatabase> {
        val mongoPassword = System.getenv("MONGO_PASSWORD")
        KMongo.createClient(
            connectionString = "mongodb+srv://micudasoftware:$mongoPassword@coockifydb.rbzh02f.mongodb.net/?retryWrites=true&w=majority"
        ).coroutine.getDatabase("cookifyDB")
    }
    singleOf(::MongoUserDataSource) { bind<UserDataSource>() }
    singleOf(::MongoUserProfileDataSource) { bind<UserProfileDataSource>() }
    singleOf(::LocalImageDataSource) { bind<ImageDataSource>() }
}