package ru.netology.backend.config

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import ru.netology.backend.model.dto.ErrorMessageDto
import ru.netology.backend.model.exception.AlreadyExistException
import ru.netology.backend.model.exception.BadRequestException
import ru.netology.backend.model.exception.NotFoundException

fun StatusPages.Configuration.exceptionHandler() {
    exception<BadRequestException> {
        call.respond(HttpStatusCode.BadRequest, ErrorMessageDto(it.getMessage()))
    }

    exception<AlreadyExistException> {
        call.respond(HttpStatusCode.BadRequest, ErrorMessageDto(it.getMessage()))
    }

    exception<NotFoundException> {
        call.respond(HttpStatusCode.NotFound, ErrorMessageDto(it.getMessage()))
    }

    exception<Throwable> {
        call.respond(HttpStatusCode.InternalServerError, ErrorMessageDto(it.getMessage()))
    }
}

fun Throwable.getMessage(): String = this.localizedMessage ?: this.message ?: ""