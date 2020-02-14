package ru.netology.backend

import io.ktor.application.Application
import io.ktor.config.MapApplicationConfig
import io.ktor.server.testing.TestApplicationEngine
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
    moduleFunction: Application.() -> Unit = Application::module,
    test: TestApplicationEngine.() -> R
) = withApplication(testEnvironment(), {}) {
    moduleFunction(application)
    test()
}