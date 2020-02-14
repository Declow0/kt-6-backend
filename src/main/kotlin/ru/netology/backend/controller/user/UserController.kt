package ru.netology.backend.controller.user

import io.ktor.application.Application
import io.ktor.routing.Route
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.AbstractKodeinController
import ru.netology.backend.service.UserService
import javax.validation.Validator

class UserController(application: Application) : AbstractKodeinController(application) {
    private val userService by kodein.instance<UserService>()
    private val validator by kodein.instance<Validator>()

    override fun Route.getRoutes() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}