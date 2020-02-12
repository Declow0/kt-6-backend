package ru.netology.backend.controller.post.attribute

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.put
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.AbstractKodeinController
import ru.netology.backend.config.isUUID
import ru.netology.backend.model.dto.PostRsDto
import ru.netology.backend.repository.PostRepository
import java.util.*

class PostShareController(application: Application) : AbstractKodeinController(application) {
    private val repo by kodein.instance<PostRepository>()

    override fun Route.getRoutes() {
        put("/{id}") {
            val idInput = call.parameters["id"]
            idInput!!.isUUID()

            call.respond(
                PostRsDto.fromModel(
                    repo.share(UUID.fromString(idInput))
                )
            )
        }
    }
}