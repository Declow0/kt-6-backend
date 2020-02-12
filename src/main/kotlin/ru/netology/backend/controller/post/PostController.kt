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
import ru.netology.backend.model.Post
import ru.netology.backend.model.dto.PostRqDto
import ru.netology.backend.model.dto.PostRsDto
import ru.netology.backend.repository.PostRepository
import java.util.*
import javax.validation.Validator

class PostController(application: Application) : AbstractKodeinController(application) {
    private val repo by kodein.instance<PostRepository>()
    private val validator by kodein.instance<Validator>()

    override fun Route.getRoutes() {
        get {
            call.respond(
                repo.getAll()
                    .map(PostRsDto.Companion::fromModel)
            )
        }

        get("/{id}") {
            val idInput = call.parameters["id"]
            idInput!!.isUUID()
            val id = UUID.fromString(idInput)
            val post = repo.get(id)

            call.respond(PostRsDto.fromModel(post))
        }

        post {
            val post = call.receive<PostRqDto>()
            post.validate(validator)

            call.respond(
                repo.put(
                    Post(
                        createdUser = post.createdUser,
                        content = post.content,
                        address = post.address,
                        location = post.location,
                        youtubeId = post.youtubeId,
                        commercialContent = java.net.URL(post.commercialContent)
                    )
                )
            )
        }

        delete("/{id}") {
            val idInput = call.parameters["id"]
            idInput!!.isUUID()
            repo.delete(UUID.fromString(idInput))

            call.respond(true)
        }
    }
}