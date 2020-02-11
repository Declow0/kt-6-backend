package ru.netology.backend.controller.post

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.AbstractKodeinController
import ru.netology.backend.config.isUUID
import ru.netology.backend.repository.PostRepository
import java.util.*
import javax.validation.Validator

class PostController(application: Application) : AbstractKodeinController(application) {
    private val repo by kodein.instance<PostRepository>()
    private val validator by kodein.instance<Validator>()

    override fun Route.getRoutes() {
        get {
            call.respond(repo.getAll())
        }

        get("/{id}") {
            call.parameters["id"]!!.isUUID()
            val id = UUID.fromString(call.parameters["id"])
            val post = repo.get(id)
            call.respond(post)
        }

        post {
            call.respond("")
        }

        delete("/{id}") {
            call.parameters["id"]!!.isUUID()
            repo.delete(UUID.fromString(call.parameters["id"]))
        }
    }
}