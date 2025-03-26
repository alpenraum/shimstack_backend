package com.alpenraum

import com.alpenraum.base.AppConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.csrf.CSRF
import io.ktor.server.response.respond
import org.koin.ktor.ext.get

fun Application.configureSecurity() {
    val appConfig: AppConfig = get()
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = appConfig.jwtAudience // "jwt-audience"
    val jwtDomain = appConfig.jwtDomain // "https://jwt-provider-domain/"
    val jwtRealm = appConfig.jwtRealm // "ktor sample app"
    val jwtSecret = appConfig.jwtSecret // "secret"
    authentication {
        jwt("jwt-auth") {
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }

            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build(),
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience) &&
                    (credential.payload.getClaim("sessionId").asString() != "")
                ) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
//    install(CSRF) {
//        // tests Origin is an expected value
//        allowOrigin("http://localhost:8080")
//
//        // tests Origin matches Host header
//        originMatchesHost()
//
//        // custom header checks
//        checkHeader("X-CSRF-Token")
//    }
}
