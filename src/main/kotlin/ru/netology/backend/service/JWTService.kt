package ru.netology.backend.service

interface JWTService {
    fun generate(username: String): String
}