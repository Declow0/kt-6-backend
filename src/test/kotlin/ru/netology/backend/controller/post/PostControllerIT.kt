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
import ru.netology.backend.addAuthToken
import ru.netology.backend.config.UUIDPatternString
import ru.netology.backend.withTestApplication
import kotlin.test.*

class PostControllerIT {

    @Test
    fun `Create and Delete Post`() = withTestApplication {
        var id: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("vasya")
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            val rs = response.content
            id = JsonPath.read(rs, "$.id")

            assertEquals("vasya", JsonPath.read(rs, "$.createdUser"))
            assertEquals("В чащах юга жил-был цитрус? Да, но фальшивый экземпляръ!", JsonPath.read(rs, "$.content"))
            assertNotNull(JsonPath.read<Long>(rs, "$.createTime"))
            assertEquals(0, JsonPath.read(rs, "$.favorite"))
            assertEquals(0, JsonPath.read(rs, "$.comment"))
            assertEquals(0, JsonPath.read(rs, "$.share"))
            assertFalse(JsonPath.read(rs, "$.favoriteByMe"))
            assertFalse(JsonPath.read(rs, "$.shareByMe"))
            assertEquals("", JsonPath.read(rs, "$.address"))
            assertEquals(55.7765289, JsonPath.read(rs, "$.location.latitude"))
            assertEquals(37.6749378, JsonPath.read(rs, "$.location.longitude"))
            assertEquals("WhWc3b3KhnY", JsonPath.read(rs, "$.youtubeId"))
            assertNull(JsonPath.read(rs, "$.commercialContent"))
            assertNull(JsonPath.read(rs, "$.original"))
            assertTrue(JsonPath.read<String>(rs, "$.id").matches(Regex(UUIDPatternString)))
            assertEquals(0, JsonPath.read(rs, "$.views"))
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
            assertEquals(
                "youtubeId: Некоректный формат индентификатора YouTube видео",
                JsonPath.read(response.content, "$.error")
            )
        }
    }

    @Test
    fun `Edit Post by User`() = withTestApplication {
        var id: String
        var createTime: Long
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("vasya")
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            id = JsonPath.read(response.content, "$.id")
            createTime = JsonPath.read<Int>(response.content, "$.createTime").toLong()
        }

        val editedPost =
            """
            {
                "content": "Новый текст. Ничего лишнего."
            }
        """.trimIndent()
        with(
            handleRequest(HttpMethod.Patch, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("vasya")
                setBody(editedPost)
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())

            val rs = response.content
            assertEquals("vasya", JsonPath.read(rs, "$.createdUser"))
            assertEquals("Новый текст. Ничего лишнего.", JsonPath.read(rs, "$.content"))
            assertEquals(createTime, JsonPath.read<Int>(rs, "$.createTime").toLong())
            assertEquals(0, JsonPath.read(rs, "$.favorite"))
            assertEquals(0, JsonPath.read(rs, "$.comment"))
            assertEquals(0, JsonPath.read(rs, "$.share"))
            assertFalse(JsonPath.read(rs, "$.favoriteByMe"))
            assertFalse(JsonPath.read(rs, "$.shareByMe"))
            assertEquals("", JsonPath.read(rs, "$.address"))
            assertNull(JsonPath.read(rs, "$.location"))
            assertNull(JsonPath.read(rs, "$.youtubeId"))
            assertNull(JsonPath.read(rs, "$.commercialContent"))
            assertNull(JsonPath.read(rs, "$.original"))
            assertEquals(id, JsonPath.read(rs, "$.id"))
            assertEquals(0, JsonPath.read(rs, "$.views"))
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
    fun `Edit Post by Another User`() = withTestApplication {
        var id: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("vasya")
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            id = JsonPath.read(response.content, "$.id")
        }

        val editedPost =
            """
            {
                "content": "Новый текст. Ничего лишнего."
            }
        """.trimIndent()
        with(
            handleRequest(HttpMethod.Patch, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("kolya")
                setBody(editedPost)
            }
        ) {
            assertEquals(HttpStatusCode.Forbidden, response.status())
            assertEquals("Can't change post of another user!", JsonPath.read(response.content, "$.error"))
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
    fun `Delete Post by Another User`() = withTestApplication {
        var id: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("vasya")
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            id = JsonPath.read(response.content, "$.id")
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("kolya")
            }
        ) {
            assertEquals(HttpStatusCode.Forbidden, response.status())
            assertEquals("Can't delete post of another user!", JsonPath.read(response.content, "$.error"))
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("vasya")
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            val json = response.content
            assertTrue(JsonPath.read(json, "$"))
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

    @Test
    fun `Get All Posts Unauthorized`() = withTestApplication {
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.Unauthorized, response.status())
        }
    }
}