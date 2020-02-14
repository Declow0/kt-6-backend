package ru.netology.backend.service

import ru.netology.backend.model.dto.PostRqDto
import ru.netology.backend.model.dto.PostRsDto
import ru.netology.backend.model.dto.RepostRqDto
import java.util.*

interface PostService {
    fun getAllAndView(): List<PostRsDto>
    fun getAndView(id: UUID): PostRsDto

    fun get(id: UUID): PostRsDto
    fun put(postRqDto: PostRqDto): PostRsDto
    suspend fun delete(id: UUID)

    suspend fun favorite(id: UUID): PostRsDto
    suspend fun unfavorite(id: UUID): PostRsDto
    suspend fun share(id: UUID): PostRsDto

    suspend fun repost(repostRqDto: RepostRqDto): PostRsDto
}