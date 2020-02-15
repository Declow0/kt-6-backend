package ru.netology.backend.service

import ru.netology.backend.model.User

interface UserService {
    fun get(username: String): User
    suspend fun auth(user: User): String
}