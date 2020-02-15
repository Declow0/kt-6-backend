package ru.netology.backend.model.dto

import com.google.gson.annotations.JsonAdapter
import ru.netology.backend.model.Location
import ru.netology.backend.model.Post
import ru.netology.backend.model.adapter.LocalDateTimeJsonAdapter
import java.net.URL
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class PostRsDto(
    val createdUser: String = "",

    val content: String = "",

    @JsonAdapter(LocalDateTimeJsonAdapter::class)
    val createTime: LocalDateTime = LocalDateTime.now(),

    val favorite: Long = 0L,
    val comment: Long = 0L,
    val share: Long = 0L,

    val favoriteByMe: Boolean = false,
    val shareByMe: Boolean = false,

    val address: String = "",
    val location: Location? = null,

    val youtubeId: String? = null,

    val commercialContent: URL? = null,

    val original: UUID? = null,
    val id: UUID = UUID.randomUUID(),
    val views: AtomicInteger = AtomicInteger()
) {
    companion object {
        fun fromModel(
            post: Post,
            favoriteCount: Long = 0,
            favoriteByMe: Boolean = false,
            commentCount: Long = 0,
            shareCount: Long = 0,
            shareByMe: Boolean = false
        ): PostRsDto = PostRsDto(
            createdUser = post.createdUser,
            content = post.content,
            createTime = post.createTime,
            favorite = favoriteCount,
            comment = commentCount,
            share = shareCount,
            favoriteByMe = favoriteByMe,
            shareByMe = shareByMe,
            address = post.address,
            location = post.location,
            youtubeId = post.youtubeId,
            commercialContent = post.commercialContent,
            original = post.original,
            id = post.id,
            views = post.views
        )
    }
}