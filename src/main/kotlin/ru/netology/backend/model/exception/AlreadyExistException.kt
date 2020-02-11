package ru.netology.backend.model.exception

import java.util.*

class AlreadyExistException(id: UUID) : RuntimeException("Post with id: $id already exist!")