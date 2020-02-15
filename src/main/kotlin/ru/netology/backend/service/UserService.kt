package ru.netology.backend.service

import ru.netology.backend.model.User

interface UserService {
    fun get(username: String): User
    suspend fun register(user: User): String
    fun authenticate(user: User): String
}