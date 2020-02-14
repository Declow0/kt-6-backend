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
import ru.netology.backend.model.dto.RepostRqDto
import ru.netology.backend.service.PostService
import javax.validation.Validator

class RepostController(application: Application) : AbstractKodeinController(application) {
    private val service by kodein.instance<PostService>()
    private val validator by kodein.instance<Validator>()

    override fun Route.getRoutes() {
        post {
            val repost = call.receive<RepostRqDto>()
            repost.validate(validator)

            call.respond(service.repost(repost))
        }
    }
}