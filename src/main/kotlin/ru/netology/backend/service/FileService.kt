package ru.netology.backend.service

import io.ktor.http.content.MultiPartData

interface FileService {
    suspend fun save(multipart: MultiPartData): String
}