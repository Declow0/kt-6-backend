package ru.netology.backend.config

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import ru.netology.backend.model.exception.AlreadyExistException
import ru.netology.backend.model.exception.BadRequestException
import ru.netology.backend.model.exception.NotFoundException

fun StatusPages.Configuration.exceptionHandler() {
    exception<BadRequestException> {
        call.respond(HttpStatusCode.BadRequest, it.getMessage())
    }

    exception<AlreadyExistException> {
        call.respond(HttpStatusCode.BadRequest, it.getMessage())
    }

    exception<NotFoundException> {
        call.respond(HttpStatusCode.NotFound, it.getMessage())
    }

    exception<Throwable> {
        call.respond(HttpStatusCode.InternalServerError, it.getMessage())
    }
}

fun Throwable.getMessage(): String = this.localizedMessage ?: this.message ?: ""