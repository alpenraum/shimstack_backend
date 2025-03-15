package com.alpenraum.controller

import io.ktor.server.routing.Routing

fun interface Controller {
    fun Routing.buildControllerRoute()
}
