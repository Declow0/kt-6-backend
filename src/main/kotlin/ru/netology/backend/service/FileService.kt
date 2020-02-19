package ru.netology.backend.service

import io.ktor.http.content.MultiPartData
import ru.netology.backend.model.dto.rs.MediaRs

interface FileService {
    suspend fun save(multipart: MultiPartData): MediaRs
}
