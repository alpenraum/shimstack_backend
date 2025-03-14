package com.alpenraum

import com.alpenraum.base.configureHTTP
import com.alpenraum.base.configureMonitoring
import com.alpenraum.di.configureKoin
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    configureKoin()
    configureSecurity()
    configureMonitoring()
    configureSockets()
    configureSerialization()
    configureHTTP()
    configureRouting()
}
