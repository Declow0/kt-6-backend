package ru.netology.backend.controller

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.AbstractKodeinController
import ru.netology.backend.model.dto.MediaRs
import ru.netology.backend.service.FileService

class MediaController(application: Application) : AbstractKodeinController(application) {
    private val fileService by kodein.instance<FileService>()

    override fun Route.getRoutes() {
        post {
            val multipart = call.receiveMultipart()

            call.respond(MediaRs(fileService.save(multipart)))
        }
    }
}