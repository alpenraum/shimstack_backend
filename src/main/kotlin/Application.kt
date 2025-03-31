package com.alpenraum

import com.alpenraum.base.AppConfig
import com.alpenraum.base.configureHTTP
import com.alpenraum.base.configureMonitoring
import com.alpenraum.di.configureKoin
import io.ktor.server.application.Application
import org.koin.core.context.GlobalContext.get
import org.koin.ktor.ext.get

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    configureKoin()

    get<AppConfig>().load(environment)

    configureSecurity()
    configureMonitoring()
    configureSockets()
    configureSerialization()
    configureHTTP()
    configureRouting()
}

