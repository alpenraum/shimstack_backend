package com.alpenraum.controller.session

import com.alpenraum.base.AppConfig
import com.alpenraum.base.getLogger
import com.alpenraum.controller.session.models.WebSocketAction
import com.alpenraum.controller.session.models.WebSocketDto
import com.alpenraum.domain.session.RideUpdate
import com.alpenraum.domain.session.SessionFlowDto
import com.alpenraum.domain.session.SessionService
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant

class AppSessionSocketHandler(
    private val sessionId: String,
) : KoinComponent {
    private val sessionService: SessionService by inject()
    private val appConfig: AppConfig by inject()
    private val logger = getLogger(this::class.java)

    suspend fun handleWebSocket(session: DefaultWebSocketSession, expiration: Instant) {
        val flow: StateFlow<SessionFlowDto> = sessionService.getOrCreateSession(sessionId)
        try {
            for (frame in session.incoming) {
                if (Instant.now().isAfter(expiration)) {
                    logger.info("Access token expired for Session $sessionId!")
                    session.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Access token is expired!"))
                }
                when (frame) {
                    is Frame.Text -> {
                        val input = Json.decodeFromString<WebSocketDto>(frame.readText())
                        logger.info("Received from client ($sessionId): $input")
                        when (input.action) {
                            WebSocketAction.GET_DATA -> {
                                if (!appConfig.isDevelopment) {
                                    session.outgoing.send(Frame.Text("Unknown command!"))
                                    continue
                                }
                                val response =
                                    flow.value.takeIf { it is SessionFlowDto.Live && it.updates.isNotEmpty() }
                                        ?.let { Json.encodeToString(it) }
                                        ?: "no data found!"
                                session.outgoing.send(Frame.Text(response))
                            }

                            WebSocketAction.RIDE_UPDATE -> {
                                handleRideUpdate(input)
                            }

                            WebSocketAction.RIDE_FINISHED -> sessionService.finishRide(sessionId)
                            else -> {}
                        }

                    }

                    is Frame.Binary -> {
                        logger.info("Received Binary from client ($sessionId): ${frame.readBytes()}")
                        session.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Binary not supported"))
                    }

                    is Frame.Close -> {
                        logger.info("Session $sessionId closed by client")
                    }

                    else -> {}
                }
            }
        } catch (e: Exception) {
            logger.error("WebSocket connection closed for $sessionId: ${e.localizedMessage}")
            session.close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, ""))
        }
    }

    private suspend fun handleRideUpdate(input: WebSocketDto) {
        if (input.jsonPayload == null) {
            logger.info("Received from client ($sessionId): empty Ride update. Ignoring...")
            return
        }
        val action = Json.decodeFromString<RideUpdate>(input.jsonPayload)
        sessionService.emitUpdate(action, sessionId)
    }
}
