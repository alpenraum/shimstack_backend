package com.alpenraum.repository

import com.alpenraum.base.getLogger
import com.alpenraum.domain.exceptions.SessionAlreadyExistsException
import com.alpenraum.domain.exceptions.SessionAlreadyFinishedException
import com.alpenraum.domain.session.RideUpdate
import com.alpenraum.domain.session.SessionFlowDto
import com.alpenraum.domain.session.SessionRepository
import io.ktor.server.plugins.*
import kotlinx.coroutines.flow.*
import kotlinx.html.Entities
import org.koin.core.annotation.Single

@Single(binds = [SessionRepository::class])
class InMemorySessionRepository : SessionRepository {
    private val logger = getLogger(this::class.java)
    private val sessionFlows = mutableMapOf<String, MutableStateFlow<SessionFlowDto>>()

    override fun getSessionFlow(sessionId: String): StateFlow<SessionFlowDto>? = sessionFlows[sessionId]

    override fun createSession(sessionId: String): StateFlow<SessionFlowDto> {
        if (sessionFlows[sessionId] != null) {
            logger.warn("session for sessionId $sessionId already exists!")
            throw SessionAlreadyExistsException()
        }
        val flow = MutableStateFlow<SessionFlowDto>(SessionFlowDto.NotYetStarted)
        sessionFlows[sessionId] = flow

        return flow
    }

    override fun deleteSession(sessionId: String) {
        sessionFlows.remove(sessionId)
    }

    override suspend fun emitUpdate(action: RideUpdate, sessionId: String) {

        sessionFlows[sessionId]?.apply {
            val list = when (val state = this.value) {
                is SessionFlowDto.NotYetStarted -> mutableListOf()
                SessionFlowDto.Finished -> throw SessionAlreadyFinishedException()
                is SessionFlowDto.Live -> state.updates
            }
            list.add(action)
            this.emit(SessionFlowDto.Live(list))
        } ?: throw NotFoundException("Session $sessionId not found!")
    }

    override suspend fun finishRide(sessionId: String) {
        sessionFlows[sessionId]?.apply {
            emit(SessionFlowDto.Finished)
        }?: throw NotFoundException("Session $sessionId not found!")
    }
}
