package com.alpenraum.domain.session

import kotlinx.coroutines.flow.MutableSharedFlow

interface SessionRepository {
    fun getSessionFlow(sessionId: String): MutableSharedFlow<String>?

    fun createSession(sessionId: String): MutableSharedFlow<String>

    fun deleteSession(sessionId: String)
}
