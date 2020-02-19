package ru.netology.backend.controller.post

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.AbstractKodeinController
import ru.netology.backend.config.isUUID
import ru.netology.backend.config.validate
import ru.netology.backend.model.dto.rq.PostRqDto
import ru.netology.backend.service.PostService
import java.util.*
import javax.validation.Validator

class PostController(application: Application) : AbstractKodeinController(application) {
    private val postService by kodein.instance<PostService>()
    private val validator by kodein.instance<Validator>()

    override fun Route.getRoutes() {
        get {
            call.respond(
                postService.getAllAndView(call.principal()!!)
            )
        }

        get("/{id}") {
            val idInput = call.parameters["id"]
            idInput!!.isUUID()
            val id = UUID.fromString(idInput)
            call.respond(postService.getAndView(id, call.principal()!!))
        }

        post {
            val post = call.receive(PostRqDto::class)
            post.validate(validator)

            call.respond(
                postService.put(post, call.principal()!!)
            )
        }

        patch("/{id}") {
            val idInput = call.parameters["id"]
            idInput!!.isUUID()

            val post = call.receive(PostRqDto::class)
            post.validate(validator)

            call.respond(
                postService.update(UUID.fromString(idInput), post, call.principal()!!)
            )
        }

        delete("/{id}") {
            val idInput = call.parameters["id"]
            idInput!!.isUUID()
            postService.delete(UUID.fromString(idInput), call.principal()!!)

            call.respond(true)
        }
    }
}
