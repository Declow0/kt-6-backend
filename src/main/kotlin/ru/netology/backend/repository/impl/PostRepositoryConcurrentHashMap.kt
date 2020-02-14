package ru.netology.backend.repository.impl

import ru.netology.backend.model.Post
import ru.netology.backend.repository.PostRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PostRepositoryConcurrentHashMap : PostRepository {
    private val repo = ConcurrentHashMap<UUID, Post>()

    override fun getAll(): List<Post> {
        return repo.values.toList()
    }

    override fun get(id: UUID): Post? {
        return repo[id]
    }

    override fun put(post: Post): Post {
        repo[post.id] = post
        return post
    }

    override fun delete(id: UUID) {
        repo.remove(id)
    }
}