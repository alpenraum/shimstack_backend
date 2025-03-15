package com.alpenraum.base

import io.ktor.server.application.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.get
import org.koin.ktor.ext.get

fun Application.configureHTTP() {
    routing {
        openAPI(path = "openapi")
    }
    if (!get<AppConfig>().isDevelopment) {
        println("Enabling https redirect since not development mode!")
        install(HttpsRedirect) {
            // The port to redirect to. By default, 443, the default HTTPS port.
            sslPort = 443
            // 301 Moved Permanently, or 302 Found redirect.
            permanentRedirect = true
        }
    }
}
