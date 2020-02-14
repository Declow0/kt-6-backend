package ru.netology.backend.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
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

    install(Authentication) {
        jwt {
            verifier(JWT.require(Algorithm.HMAC256("ASdasdasd")).build())
            validate {
                UserIdPrincipal("me")
            }
        }
    }

    install(Routing) {
        controllerConfig()
    }
}