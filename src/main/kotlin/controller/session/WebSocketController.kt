package com.alpenraum.controller.session

import com.alpenraum.base.isValidUUID
import com.alpenraum.controller.Controller
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import io.ktor.server.websocket.webSocket
import io.ktor.websocket.*

import org.koin.core.annotation.Single
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap


private val appMutexSet: MutableSet<String> = ConcurrentHashMap.newKeySet()

@Single(binds = [Controller::class])
class WebSocketController : Controller {
    override fun Routing.buildControllerRoute() {

        route("/ws") {
            authenticate("jwt-auth") {
                route("/app/{sessionId}") {
                    install(ValidateWebSocketSessionIdPlugin)
                    install(ValidateAppWebSocketPlugin)

                    webSocket {
                        val principal = call.principal<JWTPrincipal>()
                        val expiration = principal?.expiresAt?.toInstant() ?: Instant.now()

                        val sessionId = call.parameters["sessionId"] ?: run {
                            close(
                                CloseReason(CloseReason.Codes.VIOLATED_POLICY, "sessionId is required")
                            )
                            return@webSocket
                        }

                        // in theory this is unsafe since the check whether this sessionId is already connected happens way before.
                        // But I doubt that this will have a real life effect (for now), two request would need to happen within <1ms from each other to abuse this
                        with(appMutexSet) {
                            try {
                                add(sessionId)

                                val handler = AppSessionSocketHandler(sessionId)
                                handler.handleWebSocket(this@webSocket, expiration)
                            } catch (e: Throwable) {
                                throw e
                            } finally {
                                remove(sessionId)
                            }
                        }
                    }
                }
            }
            route("/observe/{sessionId}") {
                install(ValidateWebSocketSessionIdPlugin)

                webSocket {
                    val sessionId = call.parameters["sessionId"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Missing sessionId")
                        return@webSocket
                    }
                    val handler = ObserverSocketHandler(sessionId)
                    handler.handleWebSocket(this)
                }
            }
        }
    }
}

private val ValidateWebSocketSessionIdPlugin = createRouteScopedPlugin("ValidateWebSocketSessionId") {
    onCall { call ->
        val sessionId = call.parameters["sessionId"]
        if (sessionId?.isValidUUID() != true) {
            call.respond(status = HttpStatusCode.BadRequest, message = "Missing sessionId")
            return@onCall
        }
    }
}

private val ValidateAppWebSocketPlugin = createRouteScopedPlugin("ValidateAppWebSocket") {
    onCall { call ->
        val sessionId = call.parameters["sessionId"]
        if (appMutexSet.contains(sessionId)) {
            call.respond(status = HttpStatusCode.Locked, message = "Another app is already connected!")
            return@onCall
        }
    }
}
