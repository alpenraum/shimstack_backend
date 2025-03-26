package com.alpenraum.controller.session

import kotlinx.serialization.Serializable

@Serializable
enum class WebSocketAction(val debugOnly: Boolean) {
    RIDE_UPDATE(false),
    GET_DATA(true)
}