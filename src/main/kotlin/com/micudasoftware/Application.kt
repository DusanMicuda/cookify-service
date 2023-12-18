package com.micudasoftware

import com.micudasoftware.data.image.LocalImageDataSource
import com.micudasoftware.data.user.MongoUserDataSource
import com.micudasoftware.data.userprofile.MongoUserProfileDataSource
import com.micudasoftware.plugins.configureMonitoring
import com.micudasoftware.plugins.configureRouting
import com.micudasoftware.plugins.configureSecurity
import com.micudasoftware.plugins.configureSerialization
import com.micudasoftware.security.hashing.SHA256HashingService
import com.micudasoftware.security.token.JwtTokenService
import com.micudasoftware.security.token.TokenConfig
import io.ktor.server.application.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // application.conf references the main function.
fun Application.module() {
    val mongoPassword = System.getenv("MONGO_PASSWORD")
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://micudasoftware:$mongoPassword@coockifydb.rbzh02f.mongodb.net/?retryWrites=true&w=majority"
    ).coroutine.getDatabase("cookifyDB")
    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = TimeUnit.DAYS.toMillis(30),
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    val userProfileDataSource = MongoUserProfileDataSource(db)
    val imageDataSource = LocalImageDataSource()

    configureMonitoring()
    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(
        userDataSource,
        hashingService,
        tokenService,
        tokenConfig,
        userProfileDataSource,
        imageDataSource
    )
}
