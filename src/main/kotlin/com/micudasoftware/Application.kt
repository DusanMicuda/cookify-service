package com.micudasoftware

import com.micudasoftware.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // application.cong references the main function.
fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
