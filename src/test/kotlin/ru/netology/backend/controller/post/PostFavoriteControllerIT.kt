package ru.netology.backend.controller.post

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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PostFavoriteControllerIT {

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
    fun `Add Favorite`() = withTestApplication {
        `Create Post`()
        with(
            handleRequest(HttpMethod.Patch, "/api/v1/post/favorite/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1, JsonPath.read(response.content, "$.favorite"))
            assertTrue(JsonPath.read(response.content, "$.favoriteByMe"))
        }
        `Delete Post`()
    }

    @Test
    fun `Add Favorite Twice`() = withTestApplication {
        `Create Post`()
        with(
            handleRequest(HttpMethod.Patch, "/api/v1/post/favorite/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1, JsonPath.read(response.content, "$.favorite"))
            assertTrue(JsonPath.read(response.content, "$.favoriteByMe"))
        }

        with(
            handleRequest(HttpMethod.Patch, "/api/v1/post/favorite/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("Already favorite", JsonPath.read(response.content, "$.error"))
        }
        `Delete Post`()
    }

    @Test
    fun Unfavorite() = withTestApplication {
        `Create Post`()
        with(
            handleRequest(HttpMethod.Patch, "/api/v1/post/favorite/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1, JsonPath.read(response.content, "$.favorite"))
            assertTrue(JsonPath.read(response.content, "$.favoriteByMe"))
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/favorite/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(0, JsonPath.read(response.content, "$.favorite"))
            assertFalse(JsonPath.read(response.content, "$.favoriteByMe"))
        }
        `Delete Post`()
    }

    @Test
    fun `Unfavorite when not favoriteByMe`() = withTestApplication {
        `Create Post`()
        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/favorite/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("Already unfavorite", JsonPath.read(response.content, "$.error"))
        }
        `Delete Post`()
    }

    @Test
    fun `Add Favorite Two Users`() = withTestApplication {
        `Create Post`()
        with(
            handleRequest(HttpMethod.Patch, "/api/v1/post/favorite/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("vasya")
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1, JsonPath.read(response.content, "$.favorite"))
            assertTrue(JsonPath.read(response.content, "$.favoriteByMe"))
        }

        with(
            handleRequest(HttpMethod.Patch, "/api/v1/post/favorite/$postId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("kolya")
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(2, JsonPath.read(response.content, "$.favorite"))
            assertTrue(JsonPath.read(response.content, "$.favoriteByMe"))
        }
        `Delete Post`()
    }
}