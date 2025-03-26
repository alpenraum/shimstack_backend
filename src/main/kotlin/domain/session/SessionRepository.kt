package com.alpenraum.domain.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    fun getSessionFlow(sessionId: String): StateFlow<SessionFlowDto>?

    fun createSession(sessionId: String): StateFlow<SessionFlowDto>

    fun deleteSession(sessionId: String)
    suspend fun emitUpdate(action: RideUpdate, sessionId: String)
    suspend fun finishRide(sessionId: String)
}
