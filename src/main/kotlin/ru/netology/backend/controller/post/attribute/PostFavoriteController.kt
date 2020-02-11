package ru.netology.backend.controller.post.attribute

import io.ktor.application.Application
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.put
import org.kodein.di.ktor.controller.AbstractKodeinController

class PostFavoriteController(application: Application) : AbstractKodeinController(application) {
    override fun Route.getRoutes() {
        put("/{id}") {

        }

        delete("/{id}") {

        }
    }
}