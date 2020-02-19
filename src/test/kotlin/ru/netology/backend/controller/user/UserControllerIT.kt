package ru.netology.backend.controller.user

import com.jayway.jsonpath.JsonPath
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import org.junit.Test
import ru.netology.backend.withTestApplication
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserControllerIT {

    @Test
    fun `Register new User`() = withTestApplication {
        with(
            handleRequest(HttpMethod.Post, "/api/v1/registration") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "username": "ru.netology",
                        "password": "1aD23dsf"
                    }
                """.trimIndent()
                )
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(JsonPath.read(response.content, "$.token"))
        }
        return@withTestApplication
    }

    @Test
    fun `Register User with existing username`() = withTestApplication {
        with(
            handleRequest(HttpMethod.Post, "/api/v1/registration") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "username": "vasya",
                        "password": "1aD23dsf"
                    }
                """.trimIndent()
                )
            }
        ) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("User with username: vasya already exist!", JsonPath.read(response.content, "$.error"))
        }

        return@withTestApplication
    }

    @Test
    fun `Auth User Wrong Password`() = withTestApplication {
        with(
            handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "username": "vasya",
                        "password": "1aD23dasdsf"
                    }
                """.trimIndent()
                )
            }
        ) {
            assertEquals(HttpStatusCode.Unauthorized, response.status())
            assertEquals("Wrong password!", JsonPath.read(response.content, "$.error"))
        }

        return@withTestApplication
    }

    @Test
    fun `Auth User Not Exist`() = withTestApplication {
        with(
            handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    """
                    {
                        "username": "vasya123",
                        "password": "1aD23dasdsf"
                    }
                """.trimIndent()
                )
            }
        ) {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not Found User with username: vasya123", JsonPath.read(response.content, "$.error"))
        }

        return@withTestApplication
    }
}