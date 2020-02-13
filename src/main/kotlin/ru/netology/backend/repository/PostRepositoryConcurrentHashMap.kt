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

    override fun getAllAndView(): List<Post> {
        return repo.values
            .onEach { it.views.incrementAndGet() }
            .sortedByDescending { it.createTime }
    }

    override fun getAndView(id: UUID): Post {
        val post = get(id)
        post.views.incrementAndGet()
        return post
    }

    override fun get(id: UUID): Post {
        return repo[id] ?: throw NotFoundException(id)
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
        val postInRepo = get(id)
        mutex.withLock(postInRepo) {
            if (!postInRepo.favoriteByMe) {
                val post: Post = postInRepo.copy(
                    favorite = postInRepo.favorite + 1,
                    favoriteByMe = true
                )
                repo[id] = post
                return post
            } else {
                throw BadRequestException("Already favorite")
            }
        }
    }

    override suspend fun unfavorite(id: UUID): Post {
        val postInRepo = get(id)
        mutex.withLock(postInRepo) {
            if (postInRepo.favoriteByMe) {
                val post: Post = postInRepo.copy(
                    favorite = postInRepo.favorite - 1,
                    favoriteByMe = false
                )
                repo[id] = post
                return post
            } else {
                throw BadRequestException("Already unfavorite")
            }
        }
    }

    override suspend fun share(id: UUID): Post {
        val postInRepo = get(id)
        mutex.withLock(postInRepo) {
            if (!postInRepo.shareByMe) {
                val post: Post = postInRepo.copy(
                    share = postInRepo.share + 1,
                    shareByMe = true
                )
                repo[id] = post
                return post
            } else {
                throw BadRequestException("Already share")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        val post = get(id)
        mutex.withLock(post) {
            repo.remove(id)
        }
    }
}