package ru.netology.backend.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.backend.model.Post
import ru.netology.backend.model.exception.AlreadyExistException
import ru.netology.backend.model.exception.BadRequestException
import ru.netology.backend.model.exception.NotFoundException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PostRepositoryConcurrentHashMap : PostRepository {
    private val repo = ConcurrentHashMap<UUID, Post>()
    private val mutex = Mutex()

    override fun getAll(): List<Post> {
        return repo.values
            .onEach { it.views.incrementAndGet() }
            .sortedByDescending { it.createTime }
    }

    override fun get(id: UUID): Post {
        val post = repo[id] ?: throw NotFoundException(id)
        post.views.incrementAndGet()
        return post
    }

    override fun put(post: Post): Post {
        val postInRepo = repo[post.id]
        if (postInRepo != null) {
            throw AlreadyExistException(postInRepo.id)
        }
        repo[post.id] = post
        return post
    }

    override suspend fun favorite(id: UUID, increment: Boolean): Post {
        val postInRepo = repo[id] ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (!postInRepo.favoriteByMe) {
                val post: Post = postInRepo.copy(
                    favorite = postInRepo.favorite + 1,
                    favoriteByMe = true
                )
                repo[id] = post
            } else {
                throw BadRequestException("Already favorite")
            }
            return postInRepo
        }
    }

    override suspend fun unfavorite(id: UUID): Post {
        val postInRepo = repo[id] ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            // change if (byMe = true and inc = false) or (byMe = false and inc = true)
            if (postInRepo.favoriteByMe) {
                val post: Post = postInRepo.copy(
                    favorite = postInRepo.favorite - 1,
                    favoriteByMe = false
                )
                repo[id] = post
            } else {
                throw BadRequestException("Already unfavorite")
            }
            return postInRepo
        }
    }

    override suspend fun share(id: UUID): Post {
        val postInRepo = repo[id] ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (!postInRepo.shareByMe) {
                val post: Post = postInRepo.copy(
                    share = postInRepo.share + 1,
                    shareByMe = true
                )
                repo[id] = post
            }
            return postInRepo
        }
    }

    override suspend fun delete(id: UUID) {
        val post = repo[id] ?: throw NotFoundException(id)
        mutex.withLock(post) {
            repo.remove(id)
        }
    }
}