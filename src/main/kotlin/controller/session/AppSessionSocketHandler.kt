package com.alpenraum.controller.session

import com.alpenraum.base.getLogger
import com.alpenraum.domain.session.SessionService
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppSessionSocketHandler(
    private val sessionId: String,
) : KoinComponent {
    private val sessionService: SessionService by inject()
    private val logger = getLogger(this::class.java)

    suspend fun handleWebSocket(session: DefaultWebSocketSession) {
        val flow: MutableSharedFlow<String> = sessionService.getOrCreateSession(sessionId)
        try {
            for (frame in session.incoming) {
                if (frame is Frame.Text) {
                    logger.info("Received from client ($sessionId): ${frame.readText()}")
                    flow.emit(frame.readText())
                }
            }
        } catch (e: Exception) {
            logger.error("WebSocket connection closed for $sessionId: ${e.localizedMessage}")
        }
    }
}
