package com.alpenraum

import com.alpenraum.base.AppConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import org.koin.ktor.ext.get

fun Application.htmlObserver() {
    val appConfig = get<AppConfig>()
    routing {
        get("/observer/{sessionId}") {
            val sessionId = call.parameters["sessionId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            call.respondHtml {
                head {
                    unsafe {
                        +""" <link rel="stylesheet" type="text/css" href="/static/style.css">
                    <script src="/static/script.js"></script>"""
                    }
                }
                body {
                    onLoad = "siteLoaded('$sessionId','${appConfig.webSocketUrl}')"
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

    }
}
