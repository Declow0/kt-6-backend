package ru.netology.backend.controller.post

import io.ktor.application.Application
import io.ktor.routing.Route
import io.ktor.routing.post
import org.kodein.di.ktor.controller.AbstractKodeinController

class RepostController(application: Application) : AbstractKodeinController(application) {
    override fun Route.getRoutes() {
        post {

        }
    }
}