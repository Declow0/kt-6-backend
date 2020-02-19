package ru.netology.backend.service

import ru.netology.backend.model.User
import ru.netology.backend.model.dto.rs.TokenRsDto

interface UserService {
    fun get(username: String): User
    suspend fun register(user: User): TokenRsDto
    fun authenticate(user: User): TokenRsDto
}
