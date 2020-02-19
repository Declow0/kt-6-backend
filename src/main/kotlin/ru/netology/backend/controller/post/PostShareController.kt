package ru.netology.backend.controller.post

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.patch
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.AbstractKodeinController
import ru.netology.backend.config.isUUID
import ru.netology.backend.service.PostService
import java.util.*

class PostShareController(application: Application) : AbstractKodeinController(application) {
    private val postService by kodein.instance<PostService>()

    override fun Route.getRoutes() {
        patch("/{id}") {
            val idInput = call.parameters["id"]
            idInput!!.isUUID()

            call.respond(
                postService.share(UUID.fromString(idInput), call.principal()!!)
            )
        }
    }
}
