package ru.netology.backend.controller.post.attribute

import com.jayway.jsonpath.JsonPath
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.junit.Test
import ru.netology.backend.addAuthToken
import ru.netology.backend.withTestApplication
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PostShareControllerIT {

    lateinit var postId: String

    // Annotation Before works outside of TestApplicationEngine context
    fun TestApplicationEngine.`Create Post`() {
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            postId = JsonPath.read(response.content, "$.id")
        }
    }

    // Annotation After works outside of TestApplicationEngine context
    fun TestApplicationEngine.`Delete Post`() {
        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun `Add Share`() = withTestApplication {
        `Create Post`()
        with(
            handleRequest(HttpMethod.Put, "/api/v1/post/share/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1, JsonPath.read(response.content, "$.share"))
            assertTrue(JsonPath.read(response.content, "$.shareByMe"))
        }
        `Delete Post`()
    }

    @Test
    fun `Add Share Twice`() = withTestApplication {
        `Create Post`()
        with(
            handleRequest(HttpMethod.Put, "/api/v1/post/share/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1, JsonPath.read(response.content, "$.share"))
            assertTrue(JsonPath.read(response.content, "$.shareByMe"))
        }

        with(
            handleRequest(HttpMethod.Put, "/api/v1/post/share/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("Already share", JsonPath.read(response.content, "$.error"))
        }
        `Delete Post`()
    }
}