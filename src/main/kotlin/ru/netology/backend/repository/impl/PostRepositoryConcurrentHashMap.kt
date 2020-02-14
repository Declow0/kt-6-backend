package ru.netology.backend.repository.impl

import ru.netology.backend.model.Post
import ru.netology.backend.repository.PostRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PostRepositoryConcurrentHashMap : PostRepository {
    private val map = ConcurrentHashMap<UUID, Post>()

    override fun getAll(): List<Post> = map.values.toList()

    override fun get(id: UUID): Post? = map[id]

    override fun put(post: Post): Post {
        map[post.id] = post
        return post
    }

    override fun delete(id: UUID) {
        map.remove(id)
    }
}