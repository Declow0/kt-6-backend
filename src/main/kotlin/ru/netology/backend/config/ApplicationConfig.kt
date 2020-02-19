package ru.netology.backend.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
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
import ru.netology.backend.controller.MediaController
import ru.netology.backend.controller.post.PostController
import ru.netology.backend.controller.post.PostFavoriteController
import ru.netology.backend.controller.post.PostShareController
import ru.netology.backend.controller.post.RepostController
import ru.netology.backend.controller.user.UserController
import ru.netology.backend.repository.PostFavoriteRepository
import ru.netology.backend.repository.PostRepository
import ru.netology.backend.repository.PostShareRepository
import ru.netology.backend.repository.UserRepository
import ru.netology.backend.repository.impl.PostFavoriteRepositoryCHM
import ru.netology.backend.repository.impl.PostRepositoryCHM
import ru.netology.backend.repository.impl.PostShareRepositoryCHM
import ru.netology.backend.repository.impl.UserRepositoryCHM
import ru.netology.backend.service.FileService
import ru.netology.backend.service.JWTService
import ru.netology.backend.service.PostService
import ru.netology.backend.service.UserService
import ru.netology.backend.service.impl.FileServiceImpl
import ru.netology.backend.service.impl.JWTServiceImpl
import ru.netology.backend.service.impl.PostServiceImpl
import ru.netology.backend.service.impl.UserServiceImpl
import javax.validation.Validation
import javax.validation.Validator

@KtorExperimentalAPI
fun Kodein.MainBuilder.appConfig(environment: ApplicationEnvironment) {
    constant("uploadDir") with environment.config.property("application.upload.dir").getString()
    constant("jwtSecret") with environment.config.property("application.jwt.secret").getString()
    constant("jwtTTL") with environment.config.property("application.jwt.ttl").getString().toLong()

    bind<Validator>() with eagerSingleton { Validation.buildDefaultValidatorFactory().validator }

    bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
    bind<Algorithm>() with eagerSingleton { Algorithm.HMAC256(instance<String>(tag = "jwtSecret")) }
    bind<JWTVerifier>() with eagerSingleton { JWT.require(instance()).build() }
    bind<JWTService>() with eagerSingleton { JWTServiceImpl(instance(), instance(tag = "jwtTTL")) }

    bind<PostRepository>() with eagerSingleton { PostRepositoryCHM() }
    bind<PostFavoriteRepository>() with eagerSingleton { PostFavoriteRepositoryCHM() }
    bind<PostShareRepository>() with eagerSingleton { PostShareRepositoryCHM() }
    bind<UserRepository>() with eagerSingleton { UserRepositoryCHM() }

    bind<PostService>() with eagerSingleton { PostServiceImpl(instance(), instance(), instance()) }
    bind<UserService>() with eagerSingleton { UserServiceImpl(instance(), instance(), instance()) }
    bind<FileService>() with eagerSingleton { FileServiceImpl(instance(tag = "uploadDir")) }
}

fun Routing.controllerConfig() {
    route("/api/v1") {
        controller { UserController(instance()) }

        authenticate {
            static("/static") {
                val uploadDir: String by kodein().instance(tag = "uploadDir")
                files(uploadDir)
            }

            controller("/post") { PostController(instance()) }
            controller("/repost") { RepostController(instance()) }
            controller("/post/favorite") { PostFavoriteController(instance()) }
            controller("/post/share") { PostShareController(instance()) }

            controller("/media") { MediaController(instance()) }
        }
    }
}
