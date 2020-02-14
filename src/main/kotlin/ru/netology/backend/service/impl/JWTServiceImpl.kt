package ru.netology.backend.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import ru.netology.backend.service.JWTService

class JWTServiceImpl(
    private val algorithm: Algorithm
) : JWTService {

    override fun generate(username: String): String = JWT.create()
        .withSubject(username)
        .sign(algorithm)

}