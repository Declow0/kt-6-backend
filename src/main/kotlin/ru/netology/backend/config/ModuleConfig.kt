package ru.netology.backend.config

import com.auth0.jwt.JWTVerifier
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.routing.Routing
import org.kodein.di.generic.instance
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import ru.netology.backend.service.UserService

fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
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
            val verifier by kodein().instance<JWTVerifier>()
            verifier(verifier)

            val userService by kodein().instance<UserService>()
            validate {
                userService.get(it.payload.subject)
            }
        }
    }

    install(Routing) {
        controllerConfig()
    }
}
