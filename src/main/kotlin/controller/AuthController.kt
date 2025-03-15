package com.alpenraum.controller

import com.alpenraum.base.AppConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.koin.core.annotation.Single
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.ktor.ext.getKoin
import java.util.Date
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Single(binds = [Controller::class])
class AuthController : Controller {
    @OptIn(ExperimentalUuidApi::class)
    override fun Routing.buildControllerRoute() {
        val appConfig: AppConfig = getKoin().get()

        post("/auth") {
            val user = call.receive<String>()

            if (user != appConfig.authSecret) {
                call.respond(HttpStatusCode.Unauthorized)
            }
            val token =
                JWT
                    .create()
                    .withAudience(appConfig.jwtAudience)
                    .withIssuer(appConfig.jwtDomain)
                    .withClaim("sessionId", Uuid.random().toString())
                    .withExpiresAt(Date(System.currentTimeMillis() + 5.minutes.inWholeMilliseconds))
                    .sign(Algorithm.HMAC256(appConfig.jwtSecret))

            call.respond(hashMapOf("token" to token))
        }
    }
}
