package ru.netology.backend.controller.post

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import org.kodein.di.ktor.controller.AbstractKodeinController

class PostController(application: Application) : AbstractKodeinController(application) {
    override fun Route.getRoutes() {
        get("/version") {
            call.respond("asdasdasdsd")
        }
    }
}