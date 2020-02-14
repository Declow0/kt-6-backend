package ru.netology.backend.config

import io.ktor.application.ApplicationEnvironment
import io.ktor.auth.authenticate
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.routing.Routing
import io.ktor.routing.route
import io.ktor.util.KtorExperimentalAPI
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.with
import org.kodein.di.ktor.controller.controller
import org.kodein.di.ktor.kodein
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import ru.netology.backend.controller.post.PostController
import ru.netology.backend.controller.post.RepostController
import ru.netology.backend.controller.post.attribute.PostFavoriteController
import ru.netology.backend.controller.post.attribute.PostShareController
import ru.netology.backend.repository.PostRepository
import ru.netology.backend.repository.impl.PostRepositoryConcurrentHashMap
import ru.netology.backend.service.PostService
import ru.netology.backend.service.impl.PostServiceImpl
import javax.naming.ConfigurationException
import javax.validation.Validation
import javax.validation.Validator

@KtorExperimentalAPI
fun Kodein.MainBuilder.appConfig(environment: ApplicationEnvironment) {
    constant("uploadDir") with environment.config.property("application.upload.dir").getString()
    bind<PostRepository>() with eagerSingleton { PostRepositoryConcurrentHashMap() }
    bind<PostService>() with eagerSingleton { PostServiceImpl(instance()) }
    bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
    bind<Validator>() with eagerSingleton { Validation.buildDefaultValidatorFactory().validator }
}

fun Routing.controllerConfig() {
    route("/api/v1") {
        val uploadDir: String by kodein().instance(tag = "uploadDir")

        static("/static") {
            files(uploadDir)
        }

        authenticate {
            controller("/post") { PostController(instance()) }
            controller("/repost") { RepostController(instance()) }
            controller("/post/favorite") { PostFavoriteController(instance()) }
            controller("/post/share") { PostShareController(instance()) }
        }
    }
}