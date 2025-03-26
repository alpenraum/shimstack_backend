package com.alpenraum

import com.alpenraum.controller.Controller
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.li
import kotlinx.html.ul
import kotlinx.html.unsafe
import org.koin.java.KoinJavaComponent.getKoin

fun Application.configureRouting() {
    install(DoubleReceive)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
    routing {
        get("/html-dsl") {
            call.respondHtml {
                head {
                    unsafe {
                        +""" <link rel="stylesheet" type="text/css" href="/static/style.css">
                    <script src="/static/script.js"></script>"""
                    }
                }
                body {
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
    routing {
        val controllers: List<Controller> = getKoin().getAll()

        controllers.forEach {
            with(it) {
                this@routing.buildControllerRoute()
            }
        }
    }
}
