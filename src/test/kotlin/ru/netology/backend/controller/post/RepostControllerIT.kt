package ru.netology.backend.controller.post

import com.jayway.jsonpath.JsonPath
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.junit.Test
import ru.netology.backend.addAuthToken
import ru.netology.backend.config.UUIDPatternString
import ru.netology.backend.withTestApplication
import kotlin.test.*

class RepostControllerIT {

    @Test
    fun `Create Repost Null Original`() = withTestApplication {
        val rq = """
        {
        }
        """.trimIndent()

        with(
            handleRequest(HttpMethod.Post, "/api/v1/repost") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(rq)
            }
        ) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("original: не должно равняться null", JsonPath.read(response.content, "$.error"))
        }
    }

    @Test
    fun `Create Repost Not Exist Original`() = withTestApplication {
        val rq = """
        {
            "original": "46813418-0f33-4f2b-88ac-be4ca4b67ee8"
        }
        """.trimIndent()

        with(
            handleRequest(HttpMethod.Post, "/api/v1/repost") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(rq)
            }
        ) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals(
                "Original Post Not Found",
                JsonPath.read(response.content, "$.error")
            )
        }
    }

    @Test
    fun `Create Repost`() = withTestApplication {
        var original: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken("vasya")
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            original = JsonPath.read(response.content, "$.id")
        }

        val rq = """
        {
            "original": "$original"
        }
        """.trimIndent()

        var repostId: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/repost") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(rq)
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())

            val rs = response.content
            repostId = JsonPath.read(rs, "$.id")

            assertEquals("vasya", JsonPath.read(rs, "$.createdUser"))
            assertEquals("", JsonPath.read(rs, "$.content"))
            assertNotNull(JsonPath.read<Long>(rs, "$.createTime"))
            assertEquals(0, JsonPath.read(rs, "$.favorite"))
            assertEquals(0, JsonPath.read(rs, "$.comment"))
            assertEquals(0, JsonPath.read(rs, "$.share"))
            assertFalse(JsonPath.read(rs, "$.favoriteByMe"))
            assertFalse(JsonPath.read(rs, "$.shareByMe"))
            assertNull(JsonPath.read(rs, "$.location"))
            assertEquals("", JsonPath.read(rs, "$.address"))
            assertNull(JsonPath.read(rs, "$.commercialContent"))
            assertEquals(original, JsonPath.read(rs, "$.original"))
            assertTrue(JsonPath.read<String>(rs, "$.id").matches(Regex(UUIDPatternString)))
            assertEquals(0, JsonPath.read(rs, "$.views"))
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$repostId") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(JsonPath.read(response.content, "$"))
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$original") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(JsonPath.read(response.content, "$"))
        }
    }
}