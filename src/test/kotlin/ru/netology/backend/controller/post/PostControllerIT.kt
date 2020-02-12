package ru.netology.backend.controller.post

import com.jayway.jsonpath.JsonPath
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import ru.netology.backend.config.UUIDPatternString
import ru.netology.backend.config.module
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PostControllerIT {

    @Test
    fun `Create Post`() = withTestApplication(Application::module) {
        var id = ""
        with(
            handleRequest(HttpMethod.Post, "/api/v1/post") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
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
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            val json = response.content
            assertTrue(JsonPath.read(json, "$"))
        }
    }
}