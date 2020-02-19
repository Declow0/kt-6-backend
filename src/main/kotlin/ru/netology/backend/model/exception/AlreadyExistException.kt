package ru.netology.backend.model.exception

import java.util.*

class AlreadyExistException(message: String) : RuntimeException(message) {
    constructor(id: UUID) : this("Post with id: $id already exist!")
}
