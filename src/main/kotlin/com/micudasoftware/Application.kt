package com.micudasoftware

import com.micudasoftware.data.di.dataModule
import com.micudasoftware.plugins.configureMonitoring
import com.micudasoftware.plugins.configureRouting
import com.micudasoftware.plugins.configureSecurity
import com.micudasoftware.plugins.configureSerialization
import com.micudasoftware.security.di.securityModule
import com.micudasoftware.security.token.TokenConfig
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // application.conf references the main function.
fun Application.module() {
    install(Koin) {
        val appModule = org.koin.dsl.module {
            single<TokenConfig> {
                TokenConfig(
                    issuer = environment.config.property("jwt.issuer").getString(),
                    audience = environment.config.property("jwt.audience").getString(),
                    expiresIn = TimeUnit.DAYS.toMillis(30),
                    secret = System.getenv("JWT_SECRET")
                )
            }
        }
        modules(appModule, dataModule, securityModule)
    }

    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
