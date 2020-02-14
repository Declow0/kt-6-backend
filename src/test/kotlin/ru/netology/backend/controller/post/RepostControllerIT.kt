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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RepostControllerIT {

    @Test
    fun `Create Repost Null Original`() = withTestApplication {
        val rq = """
        {
            "createdUser": "Netology Group Company"
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
            assertEquals("original: не должно равняться null", response.content)
        }
    }

    @Test
    fun `Create Repost Not Exist Original`() = withTestApplication {
        val rq = """
        {
            "createdUser": "Netology Group Company",
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
                response.content
            )
        }
    }

    @Test
    fun `Create Repost`() = withTestApplication {
        var original: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(this.javaClass.getResource("/create-post.json").readText())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            original = JsonPath.read(response.content, "$.id")
        }

        val rq = """
        {
            "createdUser": "Netology Group Company",
            "original": "$original"
        }
        """.trimIndent()

        var repost: String
        with(
            handleRequest(HttpMethod.Post, "/api/v1/repost") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
                setBody(rq)
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())

            val json = response.content
            repost = JsonPath.read(json, "$.id")

            assertEquals("Netology Group Company", JsonPath.read(json, "$.createdUser"))
            assertEquals("", JsonPath.read(json, "$.content"))
            assertNotNull(JsonPath.read<Long>(json, "$.createTime"))
            assertEquals(0, JsonPath.read(json, "$.favorite"))
            assertEquals(0, JsonPath.read(json, "$.comment"))
            assertEquals(0, JsonPath.read(json, "$.share"))
            assertFalse(JsonPath.read(json, "$.favoriteByMe"))
            assertFalse(JsonPath.read(json, "$.shareByMe"))
            assertEquals("", JsonPath.read(json, "$.address"))
            assertEquals(original, JsonPath.read(json, "$.original"))
            assertTrue(JsonPath.read<String>(json, "$.id").matches(Regex(UUIDPatternString)))
            assertEquals(0, JsonPath.read(json, "$.views"))
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$repost") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addAuthToken()
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            val json = response.content
            assertTrue(JsonPath.read(json, "$"))
        }

        with(
            handleRequest(HttpMethod.Delete, "/api/v1/post/$original") {
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