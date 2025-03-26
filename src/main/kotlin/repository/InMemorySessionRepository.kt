package com.alpenraum.repository

import com.alpenraum.base.getLogger
import com.alpenraum.domain.exceptions.SessionAlreadyExistsException
import com.alpenraum.domain.session.RideUpdate
import com.alpenraum.domain.session.SessionRepository
import io.ktor.server.plugins.*
import kotlinx.coroutines.flow.*
import org.koin.core.annotation.Single

@Single(binds = [SessionRepository::class])
class InMemorySessionRepository : SessionRepository {
    private val logger = getLogger(this::class.java)
    private val sessionFlows = mutableMapOf<String, MutableStateFlow<MutableList<RideUpdate>>>()

    override fun getSessionFlow(sessionId: String): StateFlow<List<RideUpdate>>? = sessionFlows[sessionId]

    override fun createSession(sessionId: String): StateFlow<List<RideUpdate>> {
        if (sessionFlows[sessionId] != null) {
            logger.warn("session for sessionId $sessionId already exists!")
            throw SessionAlreadyExistsException()
        }
        val flow = MutableStateFlow<MutableList<RideUpdate>>(mutableListOf())
        sessionFlows[sessionId] = flow

        return flow
    }

    override fun deleteSession(sessionId: String) {
        sessionFlows.remove(sessionId)
    }

    override suspend fun emitUpdate(action: RideUpdate, sessionId: String) {

        sessionFlows[sessionId]?.apply {
            val list = this.value
            list.add(action)
            this.emit(list)
        } ?: throw NotFoundException("Session $sessionId not found!")
    }
}
