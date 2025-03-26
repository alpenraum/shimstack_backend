package com.alpenraum.controller.session

import com.alpenraum.base.AppConfig
import com.alpenraum.base.getLogger
import com.alpenraum.controller.session.models.WebSocketAction
import com.alpenraum.controller.session.models.WebSocketDto
import com.alpenraum.domain.session.SessionFlowDto
import com.alpenraum.domain.session.SessionService
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.coroutineContext

class ObserverSocketHandler(
    private val sessionId: String,
) : KoinComponent {
    private val sessionService: SessionService by inject()
    private val appConfig: AppConfig by inject()
    private val logger = getLogger(this::class.java)

    suspend fun handleWebSocket(session: DefaultWebSocketSession) {
        logger.info("Observer connected from session $sessionId")
        val flow: StateFlow<SessionFlowDto> = sessionService.getOrCreateSession(sessionId)

        val scope = CoroutineScope(coroutineContext)

        scope.launch {
            flow.collect {
                logger.info("Sending Ride update to Observer! $sessionId")
                val frame = when (it) {
                    SessionFlowDto.Finished ->Frame.Text(WebSocketAction.RIDE_FINISHED.toString())
                    is SessionFlowDto.Live -> Frame.Text(Json.encodeToString(it.updates))
                    SessionFlowDto.NotYetStarted -> Frame.Text(WebSocketAction.RIDE_NOT_YET_STARTED.toString())
                }
                session.outgoing.send(frame)
                if(it is SessionFlowDto.Finished){
                    session.close(CloseReason(CloseReason.Codes.NORMAL, "Ride finished!"))
                }
            }
        }

        try {
            for (frame in session.incoming) {

                when (frame) {
                    is Frame.Text -> {
                        val input = Json.decodeFromString<WebSocketDto>(frame.readText())
                        logger.info("Received from observer for ($sessionId): $input")

                    }

                    is Frame.Binary -> {
                        logger.info("Received Binary from Observer for ($sessionId): ${frame.readBytes()}")
                        session.close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Binary not supported"))
                    }

                    is Frame.Close -> {
                        logger.info("Observer disconnected from session $sessionId")
                    }

                    else -> {}
                }
            }
        } catch (e: Exception) {
            logger.error("WebSocket connection closed for observer for $sessionId: ${e.localizedMessage}")
            session.close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, ""))
        }
    }
}