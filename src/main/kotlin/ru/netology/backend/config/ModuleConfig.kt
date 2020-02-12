package ru.netology.backend.config

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.routing.Routing
import org.kodein.di.ktor.KodeinFeature

fun Application.module() {
    install(ContentNegotiation) {
        gson()
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