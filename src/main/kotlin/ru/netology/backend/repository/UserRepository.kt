package ru.netology.backend.repository

import ru.netology.backend.model.User

interface UserRepository {
    fun get(username: String): User?
    fun put(user: User): User
}