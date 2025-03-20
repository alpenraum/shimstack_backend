package com.alpenraum.repository

import com.alpenraum.base.getLogger
import com.alpenraum.domain.exceptions.SessionAlreadyExistsException
import com.alpenraum.domain.session.SessionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.annotation.Single

@Single(binds = [SessionRepository::class])
class InMemorySessionRepository : SessionRepository {
    private val logger = getLogger(this::class.java)
    private val sessionFlows = mutableMapOf<String, MutableSharedFlow<String>>()

    override fun getSessionFlow(sessionId: String): MutableSharedFlow<String>? = sessionFlows[sessionId]

    override fun createSession(sessionId: String): MutableSharedFlow<String> {
        if (sessionFlows[sessionId] != null) {
            logger.warn("session for sessionId $sessionId already exists!")
            throw SessionAlreadyExistsException()
        }
        val flow = MutableSharedFlow<String>()
        sessionFlows[sessionId] = flow

        return flow
    }

    override fun deleteSession(sessionId: String) {
        sessionFlows.remove(sessionId)
    }
}
