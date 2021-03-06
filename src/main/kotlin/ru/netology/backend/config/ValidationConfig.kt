package ru.netology.backend.config

import ru.netology.backend.model.exception.BadRequestException
import java.util.regex.Pattern
import javax.validation.Validator

const val UUIDPatternString = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$"
const val youTubeIDPatternString = "^[a-zA-Z0-9_-]{11}\$"
const val usernamePatternString = "^(?=.{5,20}\$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])\$"
const val passwordPatternString = "(?=.*[A-Z])(?!.*[^a-zA-Z0-9])(.{6,})\$"

fun <T : Any> T.validate(validator: Validator) {
    validator.validate(this)
        .takeIf { it.isNotEmpty() }
        ?.let {
            throw BadRequestException(
                it.joinToString("\n") { constraintViolation ->
                    "${constraintViolation?.propertyPath?.toString()}: ${constraintViolation.message}"
                }
            )
        }
}

val UUIDPattern = Pattern.compile(UUIDPatternString)
fun String.isUUID() {
    if (this.isEmpty() && !UUIDPattern.matcher(this).matches()) {
        throw BadRequestException("Invalid UUID string: $this")
    }
}
