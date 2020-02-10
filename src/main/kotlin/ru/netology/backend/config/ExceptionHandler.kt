package ru.netology.backend.config

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

fun StatusPages.Configuration.exceptionHandler() {
    exception<Throwable> {
        call.respond(HttpStatusCode.InternalServerError)
    }
}