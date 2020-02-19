package ru.netology.backend.service

import ru.netology.backend.model.User
import ru.netology.backend.model.dto.rq.PostRqDto
import ru.netology.backend.model.dto.rq.RepostRqDto
import ru.netology.backend.model.dto.rs.PostRsDto
import java.util.*

interface PostService {
    fun getAllAndView(currentUser: User): List<PostRsDto>
    fun getAndView(id: UUID, currentUser: User): PostRsDto

    fun put(postRqDto: PostRqDto, currentUser: User): PostRsDto
    fun repost(repostRqDto: RepostRqDto, currentUser: User): PostRsDto
    fun update(id: UUID, postRqDto: PostRqDto, currentUser: User): PostRsDto
    fun delete(id: UUID, currentUser: User)

    fun favorite(id: UUID, currentUser: User): PostRsDto
    fun unfavorite(id: UUID, currentUser: User): PostRsDto
    fun share(id: UUID, currentUser: User): PostRsDto
}
