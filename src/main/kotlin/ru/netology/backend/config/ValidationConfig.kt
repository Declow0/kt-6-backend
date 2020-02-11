package ru.netology.backend.config

import io.ktor.features.BadRequestException
import java.util.regex.Pattern
import javax.validation.Validator

const val UUIDPatternString = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$"
val UUIDPattern = Pattern.compile(UUIDPatternString)

fun <T : Any> T.validate(validator: Validator) {
    validator.validate(this)
        .takeIf { it.isNotEmpty() }
        ?.let { throw BadRequestException(it.first().message) }
}

fun String.isUUID() {
    if (!UUIDPattern.matcher(this).matches()) {
        throw BadRequestException("Invalid UUID string: $this")
    }
}