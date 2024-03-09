package com.micudasoftware.data.di

import com.micudasoftware.data.image.ImageDataSource
import com.micudasoftware.data.image.LocalImageDataSource
import com.micudasoftware.data.recipe.MongoRecipeDatasource
import com.micudasoftware.data.recipe.RecipeDataSource
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
        KMongo.createClient(
            connectionString = System.getenv("MONGO_CONNECTION")
        ).coroutine.getDatabase("cookifyDB")
    }
    singleOf(::MongoUserDataSource) { bind<UserDataSource>() }
    singleOf(::MongoUserProfileDataSource) { bind<UserProfileDataSource>() }
    singleOf(::LocalImageDataSource) { bind<ImageDataSource>() }
    singleOf(::MongoRecipeDatasource) { bind<RecipeDataSource>() }
}