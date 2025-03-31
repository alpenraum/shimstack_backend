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
                            <script src="https://cdn.jsdelivr.net/npm/d3@7"></script>
                            <script src="/static/script.js"></script>
                            <link rel="icon" type="image/png" href="/static/favicon/favicon-96x96.png" sizes="96x96" />
                            <link rel="icon" type="image/svg+xml" href="/static/favicon/favicon.svg" />
                            <link rel="shortcut icon" href="/static/favicon/favicon.ico" />
                            <link rel="apple-touch-icon" sizes="180x180" href="/static/favicon/apple-touch-icon.png" />
                            <link rel="manifest" href="/static/favicon/site.webmanifest" />"""
                    }
                }
                body {
                    onLoad = "siteLoaded('$sessionId','${appConfig.webSocketUrl}')"
                    h1 {
                        id = "title"
                        +"HTML"
                    }
                    div {
                        id = "chart-root"
                        div(classes = "chart") {
                            id = "speed-chart"
                        }
                        div(classes = "chart") {
                            id = "elevation-chart"
                        }
                        div(classes = "chart") {
                            id = "distance-chart"
                        }
                    }
                    unsafe {
                        +"""
                            <div id="containerVis"></div>
                            
                            
                            <table id="table">
                            <thead>
                                <tr>
                                    <th>Received RideUpdate</th>
                                </tr>
                            </thead>
                            <tbody>
                            </tbody>
                            </table>
                        """.trimIndent()
                    }
                    table {
                        id = "table"
                    }
                }
            }
        }

    }
}
