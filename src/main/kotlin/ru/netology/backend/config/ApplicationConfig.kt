package ru.netology.backend.config

import io.ktor.routing.Routing
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.controller
import ru.netology.backend.controller.post.PostController

fun Kodein.MainBuilder.appConfig() {
}

fun Routing.controllerConfig() {
    controller { PostController(instance()) }
}