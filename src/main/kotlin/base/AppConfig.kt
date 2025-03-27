package com.alpenraum.base

import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.ApplicationConfig
import org.koin.core.annotation.Single

@Single
class AppConfig {
    lateinit var config: ApplicationConfig

    fun load(environment: ApplicationEnvironment) {
        config = environment.config
    }

    val isDevelopment: Boolean get() = config.property("ktor.development").getString() == "true"

    val serverPort: Int get() = config.property("ktor.deployment.port").getString().toInt()
    val jwtDomain: String get() = config.property("jwt.domain").getString()
    val jwtAudience: String get() = config.property("jwt.audience").getString()
    val jwtRealm: String get() = config.property("jwt.realm").getString()
    val authSecret: String get() = config.property("auth.secret").getString()
    val jwtSecret: String get() = config.property("jwt.secret").getString()
    val webSocketUrl: String get() = config.property("html.webSocketUrl").getString()
}
