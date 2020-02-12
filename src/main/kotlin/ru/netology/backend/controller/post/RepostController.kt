package ru.netology.backend.controller.post

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.AbstractKodeinController
import ru.netology.backend.config.validate
import ru.netology.backend.model.Post
import ru.netology.backend.model.dto.PostRsDto
import ru.netology.backend.model.dto.RepostRqDto
import ru.netology.backend.repository.PostRepository
import java.util.*
import javax.validation.Validator

class RepostController(application: Application) : AbstractKodeinController(application) {
    private val repo by kodein.instance<PostRepository>()
    private val validator by kodein.instance<Validator>()

    override fun Route.getRoutes() {
        post {
            val repost = call.receive<RepostRqDto>()
            repost.validate(validator)

            call.respond(
                PostRsDto.fromModel(
                    repo.put(
                        Post(
                            createdUser = repost.createdUser,
                            content = repost.content,
                            address = repost.address,
                            location = repost.location,
                            youtubeId = repost.youtubeId,
                            commercialContent = java.net.URL(repost.commercialContent),
                            original = UUID.fromString(repost.original)
                        )
                    )
                )
            )
        }
    }
}