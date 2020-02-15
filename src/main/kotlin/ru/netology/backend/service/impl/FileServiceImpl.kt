package ru.netology.backend.service.impl

import io.ktor.features.UnsupportedMediaTypeException
import io.ktor.http.ContentType
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.backend.model.exception.BadRequestException
import ru.netology.backend.service.FileService
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class FileServiceImpl(private val uploadPath: String) : FileService {
    private val images = listOf(ContentType.Image.JPEG, ContentType.Image.PNG)

    init {
        println(Paths.get(uploadPath).toAbsolutePath().toString())
        if (Files.notExists(Paths.get(uploadPath))) {
            Files.createDirectory(Paths.get(uploadPath))
        }
    }

    override suspend fun save(multipart: MultiPartData): String {
        var response: String? = null
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    if (part.name == "file") {
                        if (!images.contains(part.contentType)) {
                            throw UnsupportedMediaTypeException(part.contentType ?: ContentType.Any)
                        }
                        val ext = when (part.contentType) {
                            ContentType.Image.JPEG -> "jpg"
                            ContentType.Image.PNG -> "png"
                            else -> throw UnsupportedMediaTypeException(part.contentType!!)
                        }
                        val fileUUID = "${UUID.randomUUID()}.$ext"
                        val path = Paths.get(uploadPath, fileUUID)
                        part.streamProvider().use {
                            withContext(Dispatchers.IO) {
                                Files.copy(it, path)
                            }
                        }
                        part.dispose()
                        response = fileUUID
                        return@forEachPart
                    }
                }
            }

            part.dispose()
        }
        return response ?: throw BadRequestException("No file field in request")
    }
}