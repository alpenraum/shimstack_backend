package com.alpenraum.domain.session

import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.annotation.Single

@Single
class SessionService(
    private val sessionRepository: SessionRepository,
) {
    fun getOrCreateSession(sessionId: String): MutableSharedFlow<String> =
        sessionRepository.getSessionFlow(sessionId) ?: sessionRepository.createSession(sessionId)
}
