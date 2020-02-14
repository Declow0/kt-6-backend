package ru.netology.backend.config

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.routing.Routing
import org.kodein.di.ktor.KodeinFeature

fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(StatusPages) {
        exceptionHandler()
    }

    install(KodeinFeature) {
        appConfig(environment)
    }

    install(Routing) {
        controllerConfig()
    }

//    install(Authentication) {
//        jwt {
//
//        }
//    }
}