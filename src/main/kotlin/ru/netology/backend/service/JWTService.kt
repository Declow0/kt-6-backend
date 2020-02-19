package ru.netology.backend.service

interface JWTService {
    fun generateAuthToken(username: String): String
}
