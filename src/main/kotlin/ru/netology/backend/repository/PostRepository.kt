package ru.netology.backend.repository

import ru.netology.backend.model.Post
import java.util.*

interface PostRepository {
    fun getAllAndView(): List<Post>
    fun getAndView(id: UUID): Post
    fun get(id: UUID): Post
    fun put(post: Post): Post
    suspend fun favorite(id: UUID, increment: Boolean = true): Post
    suspend fun unfavorite(id: UUID): Post
    suspend fun share(id: UUID): Post
    suspend fun delete(id: UUID)
}