package ru.netology.backend.service

import ru.netology.backend.model.User

interface UserService {
    fun get(username: String): User
    suspend fun put(user: User): User
}