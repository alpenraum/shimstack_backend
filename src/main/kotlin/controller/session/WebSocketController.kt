package com.alpenraum.controller.session

import com.alpenraum.controller.Controller
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import org.koin.core.annotation.Single

@Single(binds = [Controller::class])
class WebSocketController : Controller {
    override fun Routing.buildControllerRoute() {
        route("/ws") {
            authenticate("jwt-auth") {
                webSocket("/app/{sessionId}") {
                    TODO()
                }
            }
            webSocket("/observe/{sessionId}") {
                TODO()
            }
        }
    }
}
