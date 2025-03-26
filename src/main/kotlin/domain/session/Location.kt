package com.alpenraum.domain.session

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
class Location(
    val latitude: Double,
    val longitude: Double,
    val speedInKph: Float,
    val altitude: Double,
    val accuracy: Float,
    val timestamp: Instant
)

@Serializable
class RideUpdate(
    val startTime: Instant,
    val endTime: Instant? = null,
    val totalDistance: Float,
    val totalElevation: Float,
    val averageSpeed: Float,
    val topSpeed: Float,
    val locations: List<Location>
)