package ru.netology.backend.service

import ru.netology.backend.model.User
import ru.netology.backend.model.dto.PostRqDto
import ru.netology.backend.model.dto.PostRsDto
import ru.netology.backend.model.dto.RepostRqDto
import java.util.*

interface PostService {
    fun getAllAndView(currentUser: User): List<PostRsDto>
    fun getAndView(id: UUID, currentUser: User): PostRsDto

    fun put(postRqDto: PostRqDto, currentUser: User): PostRsDto
    fun repost(repostRqDto: RepostRqDto, currentUser: User): PostRsDto
    suspend fun update(postRqDto: PostRqDto, currentUser: User): PostRsDto
    suspend fun delete(id: UUID, currentUser: User)

    suspend fun favorite(id: UUID, currentUser: User): PostRsDto
    suspend fun unfavorite(id: UUID, currentUser: User): PostRsDto
    suspend fun share(id: UUID, currentUser: User): PostRsDto
}