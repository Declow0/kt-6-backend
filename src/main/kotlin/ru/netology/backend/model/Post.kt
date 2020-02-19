package ru.netology.backend.model

import java.net.URL
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

data class Post(
    val createdUser: String = "",

    val content: String = "",

    val createTime: LocalDateTime = LocalDateTime.now(),

    val repost: AtomicLong = AtomicLong(),

    val address: String = "",
    val location: Location? = null,

    val youtubeId: String? = null,

    val commercialContent: URL? = null,

    val original: UUID? = null,
    val id: UUID = UUID.randomUUID(),
    val views: AtomicLong = AtomicLong()
)
