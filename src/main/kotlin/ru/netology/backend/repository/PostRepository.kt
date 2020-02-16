package ru.netology.backend.repository

import ru.netology.backend.model.Post
import java.util.*

interface PostRepository {
    fun getAll(): List<Post>
    fun get(id: UUID): Post?
    fun put(post: Post): Post
    fun delete(id: UUID)
}