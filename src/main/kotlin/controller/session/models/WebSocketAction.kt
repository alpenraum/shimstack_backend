package com.alpenraum.controller.session.models

import kotlinx.serialization.Serializable

@Serializable
enum class WebSocketAction(val debugOnly: Boolean) {
    RIDE_UPDATE(false),
    RIDE_FINISHED(false),
    RIDE_NOT_YET_STARTED(false),

    // region debug
    GET_DATA(true),

    // endregion

}