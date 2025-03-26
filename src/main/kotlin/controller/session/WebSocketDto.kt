package com.alpenraum.controller.session

import kotlinx.serialization.Serializable

// TODO: Move jsonPayload to custom deserializer to support type safety and automatic deserializing. Not required for MVP
@Serializable
class WebSocketDto(val action: WebSocketAction, val jsonPayload: String? = null)