package ru.netology.backend.config

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import org.kodein.di.ktor.KodeinFeature

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
        }
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