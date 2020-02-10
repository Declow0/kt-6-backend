package ru.netology.backend

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.netty.EngineMain
import org.kodein.di.ktor.KodeinFeature
import ru.netology.backend.config.appConfig
import ru.netology.backend.config.controllerConfig
import ru.netology.backend.config.exceptionHandler

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson()
    }

    install(StatusPages) {
        exceptionHandler()
    }

    install(KodeinFeature) {
        appConfig()
    }

    install(Routing) {
        controllerConfig()
    }
}