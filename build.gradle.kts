
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"
}

group = "com.alpenraum"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.kotlinx.html)
    implementation(libs.ktor.server.websockets)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.http.redirect)
    implementation(libs.ktor.server.double.receive)
    implementation(libs.ktor.server.csrf)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation(platform("io.insert-koin:koin-annotations-bom:2.0.0"))
    implementation("io.insert-koin:koin-annotations")
    ksp("io.insert-koin:koin-ksp-compiler:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
}

ksp { arg("KOIN_DEFAULT_MODULE", "true") }
