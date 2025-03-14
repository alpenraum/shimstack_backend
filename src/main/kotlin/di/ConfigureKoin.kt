package com.alpenraum.di

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(ShimstackBackendGeneratedModule().module)
    }
}

@Module
@ComponentScan
class ShimstackBackendGeneratedModule
