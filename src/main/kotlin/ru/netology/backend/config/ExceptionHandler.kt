package ru.netology.backend.config

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.features.UnsupportedMediaTypeException
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import ru.netology.backend.model.dto.rs.ErrorMessageRsDto
import ru.netology.backend.model.exception.*

fun StatusPages.Configuration.exceptionHandler() {
    exception<BadRequestException> {
        call.respond(HttpStatusCode.BadRequest, ErrorMessageRsDto(it.getMessage()))
    }

    exception<UnsupportedMediaTypeException> {
        call.respond(
            HttpStatusCode.UnsupportedMediaType,
            ErrorMessageRsDto(it.getMessage())
        )
    }

    exception<AlreadyExistException> {
        call.respond(HttpStatusCode.BadRequest, ErrorMessageRsDto(it.getMessage()))
    }

    exception<NotFoundException> {
        call.respond(HttpStatusCode.NotFound, ErrorMessageRsDto(it.getMessage()))
    }

    exception<AccessDeniedException> {
        call.respond(HttpStatusCode.Forbidden, ErrorMessageRsDto(it.getMessage()))
    }

    exception<InvalidPasswordException> {
        call.respond(HttpStatusCode.Unauthorized, ErrorMessageRsDto(it.getMessage()))
    }

    exception<Throwable> {
        call.respond(
            HttpStatusCode.InternalServerError,
            ErrorMessageRsDto(it.getMessage())
        )
    }
}

fun Throwable.getMessage(): String = this.localizedMessage ?: this.message ?: ""
