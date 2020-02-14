package ru.netology.backend.model.exception

import java.util.*

class NotFoundException(message: String) : RuntimeException(message) {
    constructor(id: UUID) : this("Not found post with id: $id")
}