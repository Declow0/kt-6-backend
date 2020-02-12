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
        fun fromModel(post: Post): PostRsDto = PostRsDto(
            createdUser = post.createdUser,
            content = post.content,
            createTime = post.createTime,
            favorite = post.favorite,
            comment = post.comment,
            share = post.share,
            favoriteByMe = post.favoriteByMe,
            shareByMe = post.shareByMe,
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