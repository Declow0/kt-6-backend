package ru.netology.backend.controller.post

import com.jayway.jsonpath.JsonPath
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import net.minidev.json.JSONArray
import org.junit.Test
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import ru.netology.backend.addAuthToken
import ru.netology.backend.config.UUIDPatternString
import ru.netology.backend.withTestApplication
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PostControllerIT {

    @Test
    fun `Create Post`() = withTestApplication {
        var id: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            val json = response.content
            id = JsonPath.read(json, "$.id")

            assertEquals("Netology Group Company", JsonPath.read(json, "$.createdUser"))
            assertEquals("В чащах юга жил-был цитрус? Да, но фальшивый экземпляръ!", JsonPath.read(json, "$.content"))
            assertNotNull(JsonPath.read<Long>(json, "$.createTime"))
            assertEquals(0, JsonPath.read(json, "$.favorite"))
            assertEquals(0, JsonPath.read(json, "$.comment"))
            assertEquals(0, JsonPath.read(json, "$.share"))
            assertFalse(JsonPath.read(json, "$.favoriteByMe"))
            assertFalse(JsonPath.read(json, "$.shareByMe"))
            assertEquals("", JsonPath.read(json, "$.address"))
            assertEquals(55.7765289, JsonPath.read(json, "$.location.latitude"))
            assertEquals(37.6749378, JsonPath.read(json, "$.location.longitude"))
            assertEquals("WhWc3b3KhnY", JsonPath.read(json, "$.youtubeId"))
            assertTrue(JsonPath.read<String>(json, "$.id").matches(Regex(UUIDPatternString)))
            assertEquals(0, JsonPath.read(json, "$.views"))
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            val json = response.content
            assertTrue(JsonPath.read(json, "$"))
        }
    }

    @Test
    fun `Create Post Empty Content`() = withTestApplication {
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(this.javaClass.getResource("/create-post-empty-content.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("content: не должно быть пустым", JsonPath.read(response.content, "$.error"))
        }
    }

    @Test
    fun `Create Post Incorrect youtubeId`() = withTestApplication {
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(this.javaClass.getResource("/create-post-incorrect-youtube-id.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("youtubeId: должно соответствовать \"[a-zA-Z0-9_-]{11}\"", JsonPath.read(response.content, "$.error"))
        }
    }

    @Test
    fun `Delete Non Exist Post`() = withTestApplication {
        val id = "46813418-0f33-4f2b-88ac-be4ca4b67ee8"
        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not found post with id: $id", JsonPath.read(response.content, "$.error"))
        }
    }

    @Test
    fun `Get Non Exist Post`() = withTestApplication {
        val id = "46813418-0f33-4f2b-88ac-be4ca4b67ee8"
        with(
            handleRequest(HttpMethod.Get, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not found post with id: $id", JsonPath.read(response.content, "$.error"))
        }
    }

    @Test
    fun `Get with Created Post Increment View`() = withTestApplication {
        var id: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            id = JsonPath.read(response.content, "$.id")
        }

        with(
            handleRequest(HttpMethod.Get, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1, JsonPath.read(response.content, "$.views"))
        }

        with(
            handleRequest(HttpMethod.Get, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(2, JsonPath.read(response.content, "$.views"))
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            val json = response.content
            assertTrue(JsonPath.read(json, "$"))
        }
    }

    @Test
    fun `Get All Created Post Increment View`() = withTestApplication {
        var id: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            id = JsonPath.read(response.content, "$.id")
        }

        with(
            handleRequest(HttpMethod.Get, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals(1, JsonPath.read<JSONArray>(response.content, "$[*].id").size)
            assertEquals(1, JsonPath.read(response.content, "$[0].views"))
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            val json = response.content
            assertTrue(JsonPath.read(json, "$"))
        }
    }
}