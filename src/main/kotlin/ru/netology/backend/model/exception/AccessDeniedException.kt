package ru.netology.backend.model.exception

import java.lang.RuntimeException

class AccessDeniedException(message: String): RuntimeException(message)