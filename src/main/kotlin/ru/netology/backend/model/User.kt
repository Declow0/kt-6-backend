package ru.netology.backend.model

import io.ktor.auth.Principal

data class User(
    val username: String = "",
    val password: String = ""
) : Principal