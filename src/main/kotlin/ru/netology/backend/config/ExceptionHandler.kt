package ru.netology.backend.config

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.features.UnsupportedMediaTypeException
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import ru.netology.backend.model.dto.ErrorMessageDto
import ru.netology.backend.model.exception.AlreadyExistException
import ru.netology.backend.model.exception.BadRequestException
import ru.netology.backend.model.exception.InvalidPasswordException
import ru.netology.backend.model.exception.NotFoundException

fun StatusPages.Configuration.exceptionHandler() {
    exception<BadRequestException> {
        call.respond(HttpStatusCode.BadRequest, ErrorMessageDto(it.getMessage()))
    }

    exception<UnsupportedMediaTypeException> {
        call.respond(HttpStatusCode.UnsupportedMediaType, ErrorMessageDto(it.getMessage()))
    }

    exception<AlreadyExistException> {
        call.respond(HttpStatusCode.BadRequest, ErrorMessageDto(it.getMessage()))
    }

    exception<NotFoundException> {
        call.respond(HttpStatusCode.NotFound, ErrorMessageDto(it.getMessage()))
    }

    exception<AccessDeniedException> {
        call.respond(HttpStatusCode.Forbidden, ErrorMessageDto(it.getMessage()))
    }

    exception<InvalidPasswordException> {
        call.respond(HttpStatusCode.Forbidden, ErrorMessageDto(it.getMessage()))
    }

    exception<Throwable> {
        call.respond(HttpStatusCode.InternalServerError, ErrorMessageDto(it.getMessage()))
    }
}

fun Throwable.getMessage(): String = this.localizedMessage ?: this.message ?: ""