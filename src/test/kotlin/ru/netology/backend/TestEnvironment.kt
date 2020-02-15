package ru.netology.backend

import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpHeaders
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.withApplication
import kotlinx.coroutines.runBlocking
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import ru.netology.backend.config.module
import ru.netology.backend.model.User
import ru.netology.backend.service.JWTService
import ru.netology.backend.service.UserService
import java.nio.file.Files

fun testEnvironment() = createTestEnvironment {
    (config as MapApplicationConfig).apply {
        put("application.upload.dir", Files.createTempDirectory("uploads").toString())
        put("application.jwt.secret", "5c2dbef6-289c-46e6-8cfd-d8b3292d373a")
    }
}

fun <R> withTestApplication(
    test: TestApplicationEngine.() -> R
) = withApplication(testEnvironment()) {
    application.module()
    val userService by application.kodein().instance<UserService>()
    runBlocking {
        userService.auth(User("vasya", "password"))
        userService.auth(User("kolya", "password"))
    }
    test()
}

fun TestApplicationRequest.addAuthToken(username: String = "vasya") {
    val jwtService by call.kodein().instance<JWTService>()
    addHeader(
        HttpHeaders.Authorization,
        "Bearer ${jwtService.generateAuthToken(username)}"
    )
}