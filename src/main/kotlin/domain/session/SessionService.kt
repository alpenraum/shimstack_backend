package com.alpenraum.domain.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import org.koin.core.annotation.Single

@Single
class SessionService(
    private val sessionRepository: SessionRepository,
) {
    fun getOrCreateSession(sessionId: String): StateFlow<SessionFlowDto> =
        sessionRepository.getSessionFlow(sessionId) ?: sessionRepository.createSession(sessionId)

    suspend fun emitUpdate(action: RideUpdate, sessionId: String) {
        sessionRepository.emitUpdate(action, sessionId)
    }

    suspend fun finishRide(sessionId: String) {
        sessionRepository.finishRide(sessionId)
    }
}
