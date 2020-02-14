package ru.netology.backend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpHeaders
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.withApplication
import ru.netology.backend.config.module
import java.nio.file.Files

fun testEnvironment() = createTestEnvironment {
    (config as MapApplicationConfig).apply {
        put("application.upload.dir", Files.createTempDirectory("uploads").toString())
    }
}

fun <R> withTestApplication(
    test: TestApplicationEngine.() -> R
) = withApplication(testEnvironment()) {
    application.module()
    test()
}

fun TestApplicationRequest.addAuthToken() {
    addHeader(
        HttpHeaders.Authorization,
        "Bearer ${JWT.create().sign(Algorithm.HMAC256("ASdasdasd"))}"
    )
}