package com.alpenraum.controller.session

import com.alpenraum.controller.Controller
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import org.koin.ktor.ext.get
import java.time.Instant

@Single(binds = [Controller::class])
class WebSocketController : Controller {
    override fun Routing.buildControllerRoute() {
        route("/ws") {
            authenticate("jwt-auth") {
                webSocket("/app/{sessionId}") {
                    val principal = call.principal<JWTPrincipal>()
                    val expiration = principal?.expiresAt?.toInstant() ?: Instant.now()


                    val sessionId = call.parameters["sessionId"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Missing sessionId")
                        return@webSocket
                    }
                    val handler = AppSessionSocketHandler(sessionId)
                    handler.handleWebSocket(this,expiration)
                }
            }
            webSocket("/observe/{sessionId}") {
                TODO()
            }
        }
    }
}
