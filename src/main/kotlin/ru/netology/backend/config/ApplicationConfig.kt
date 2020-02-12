package ru.netology.backend.config

import io.ktor.routing.Routing
import io.ktor.routing.route
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.ktor.controller.controller
import ru.netology.backend.controller.post.PostController
import ru.netology.backend.controller.post.RepostController
import ru.netology.backend.controller.post.attribute.PostFavoriteController
import ru.netology.backend.controller.post.attribute.PostShareController
import ru.netology.backend.repository.PostRepository
import ru.netology.backend.repository.PostRepositoryConcurrentHashMap
import javax.validation.Validation
import javax.validation.Validator

fun Kodein.MainBuilder.appConfig() {
    bind<PostRepository>() with eagerSingleton { PostRepositoryConcurrentHashMap() }
    bind<Validator>() with eagerSingleton { Validation.buildDefaultValidatorFactory().validator }
}

fun Routing.controllerConfig() {
    route("/api/v1") {
        controller("/post") { PostController(instance()) }
        controller("/repost") { RepostController(instance()) }
        controller("/post/favorite") { PostFavoriteController(instance()) }
        controller("/post/share") { PostShareController(instance()) }
    }
}