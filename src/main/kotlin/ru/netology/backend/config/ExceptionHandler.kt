package ru.netology.backend.config

import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import ru.netology.backend.model.exception.AlreadyExistException
import ru.netology.backend.model.exception.NotFoundException

fun StatusPages.Configuration.exceptionHandler() {
    exception<BadRequestException> {
        call.respond(HttpStatusCode.BadRequest, it.localizedMessage)
    }

    exception<AlreadyExistException> {
        call.respond(HttpStatusCode.BadRequest, it.localizedMessage)
    }

    exception<NotFoundException> {
        call.respond(HttpStatusCode.NotFound, it.localizedMessage)
    }

    exception<Throwable> {
        call.respond(HttpStatusCode.InternalServerError, it.localizedMessage)
    }
}