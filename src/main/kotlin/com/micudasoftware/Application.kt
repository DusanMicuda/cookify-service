package com.micudasoftware

import com.micudasoftware.data.user.MongoUserDataSource
import com.micudasoftware.plugins.configureMonitoring
import com.micudasoftware.plugins.configureRouting
import com.micudasoftware.plugins.configureSecurity
import com.micudasoftware.plugins.configureSerialization
import io.ktor.server.application.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // application.cong references the main function.
fun Application.module() {
    val mongoPassword = System.getenv("MONGO_PASSWORD")
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://micudasoftware:$mongoPassword@coockifydb.rbzh02f.mongodb.net/?retryWrites=true&w=majority"
    ).coroutine.getDatabase("cookifyDB")
    val userDataSource = MongoUserDataSource(db)

    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
