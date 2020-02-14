package ru.netology.backend.controller.post

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.AbstractKodeinController
import ru.netology.backend.config.isUUID
import ru.netology.backend.config.validate
import ru.netology.backend.model.dto.PostRqDto
import ru.netology.backend.service.PostService
import java.util.*
import javax.validation.Validator

class PostController(application: Application) : AbstractKodeinController(application) {
    private val service by kodein.instance<PostService>()
    private val validator by kodein.instance<Validator>()

    override fun Route.getRoutes() {
        get {
            call.respond(
                service.getAllAndView()
            )
        }

        get("/{id}") {
            val idInput = call.parameters["id"]
            idInput!!.isUUID()
            val id = UUID.fromString(idInput)
            call.respond(service.getAndView(id))
        }

        post {
            val post = call.receive<PostRqDto>()
            post.validate(validator)

            call.respond(
                service.put(post)
            )
        }

        delete("/{id}") {
            val idInput = call.parameters["id"]
            idInput!!.isUUID()
            service.delete(UUID.fromString(idInput))

            call.respond(true)
        }
    }
}