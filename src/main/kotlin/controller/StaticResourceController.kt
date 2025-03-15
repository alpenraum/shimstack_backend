package com.alpenraum.controller

import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing
import org.koin.core.annotation.Single

@Single(binds = [Controller::class])
class StaticResourceController : Controller {
    override fun Routing.buildControllerRoute() {
        println("setting static resources!")
        staticResources("/static", "static", index = null)
    }
}
