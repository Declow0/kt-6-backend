package ru.netology.backend.controller.user

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.AbstractKodeinController
import ru.netology.backend.config.validate
import ru.netology.backend.model.User
import ru.netology.backend.service.UserService
import javax.validation.Validator

class RegisterUserController(application: Application) : AbstractKodeinController(application) {
    private val userService by kodein.instance<UserService>()
    private val validator by kodein.instance<Validator>()

    override fun Route.getRoutes() {
        post {
            val user = call.receive<User>()
            user.validate(validator)

            call.respond(userService.auth(user))
        }
    }
}