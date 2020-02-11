package ru.netology.backend.model

import ru.netology.backend.config.UUIDPatternString
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern

data class Post(
    @NotEmpty
    val createdUser: String = "",

    @NotEmpty
    val content: String = "",

    val createTime: LocalDateTime = LocalDateTime.now(),

    val favorite: Long = 0L,
    val disliked: Long = 0L,
    val comment: Long = 0L,
    val share: Long = 0L,

    val favoriteByMe: Boolean = false,
    val shareByMe: Boolean = false,

    val address: String = "",
    val location: Location? = null,

    val youtubeId: String? = null,

    @org.hibernate.validator.constraints.URL
    val commercialContent: URL? = null,

    @Pattern(regexp = UUIDPatternString)
    val original: UUID? = null,
    @Pattern(regexp = UUIDPatternString)
    val id: UUID = UUID.randomUUID(),
    val views: AtomicInteger = AtomicInteger()
)