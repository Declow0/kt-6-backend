package ru.netology.backend.controller

import com.jayway.jsonpath.JsonPath
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.utils.io.streams.asInput
import org.junit.Test
import ru.netology.backend.addAuthToken
import ru.netology.backend.config.UUIDPatternString
import ru.netology.backend.withTestApplication
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MediaControllerIT {
    @Test
    fun `Test File Upload And Get`() = withTestApplication {
        val id: String
        with(handleRequest(HttpMethod.Post, "/api/v1/media") {
            addHeader(
                HttpHeaders.ContentType,
                ContentType.MultiPart.FormData.withParameter("boundary", "***blob***").toString()
            )
            addAuthToken()
            setBody(
                "***blob***",
                listOf(
                    PartData.FileItem({
                        Files.newInputStream(
                            Paths.get(this.javaClass.getResource("/test.png").toURI())
                        ).asInput()
                    }, {}, headersOf(
                        HttpHeaders.ContentDisposition to listOf(
                            ContentDisposition.File.withParameter(
                                ContentDisposition.Parameters.Name,
                                "file"
                            ).toString(),
                            ContentDisposition.File.withParameter(
                                ContentDisposition.Parameters.FileName,
                                "test.png"
                            ).toString()
                        ),
                        HttpHeaders.ContentType to listOf(ContentType.Image.PNG.toString())
                    )
                    )
                )
            )
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            id = JsonPath.read(response.content, "$.id")
            val (uuid, ext) = id.split(".")
            assertTrue(uuid.matches(Regex(UUIDPatternString)))
            assertEquals("png", ext)
        }

        with(handleRequest(HttpMethod.Get, "/api/v1/static/$id") {
            addHeader(
                HttpHeaders.ContentType,
                ContentType.Image.PNG.toString()
            )
            addAuthToken()
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }
}