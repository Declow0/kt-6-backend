package ru.netology.backend.model

import io.ktor.auth.Principal
import ru.netology.backend.config.passwordPatternString
import ru.netology.backend.config.usernamePatternString
import javax.validation.constraints.Pattern

data class User(
    @field:Pattern(regexp = usernamePatternString)
    val username: String = "",
    @field:Pattern(regexp = passwordPatternString)
    val password: String = ""
) : Principal