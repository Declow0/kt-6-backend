package ru.netology.backend.model.exception

import java.util.*

class NotFoundException(id: UUID) : RuntimeException("Not found post with id: $id")