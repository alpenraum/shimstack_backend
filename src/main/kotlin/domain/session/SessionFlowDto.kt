package com.alpenraum.domain.session

sealed interface SessionFlowDto {
    object NotYetStarted : SessionFlowDto
    class Live(val updates: MutableList<RideUpdate>): SessionFlowDto
    object Finished : SessionFlowDto
}