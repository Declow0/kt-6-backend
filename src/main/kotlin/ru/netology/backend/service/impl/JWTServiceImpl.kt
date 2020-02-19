package ru.netology.backend.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import ru.netology.backend.service.JWTService
import java.util.*

class JWTServiceImpl(
    private val algorithm: Algorithm,
    private val ttl: Long
) : JWTService {

    override fun generateAuthToken(username: String): String = JWT.create()
        .withSubject(username)
        .withExpiresAt(Date(System.currentTimeMillis() + ttl))
        .sign(algorithm)

}
