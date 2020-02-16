package ru.netology.backend.service.impl

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.backend.model.Post
import ru.netology.backend.model.User
import ru.netology.backend.model.dto.rq.PostRqDto
import ru.netology.backend.model.dto.rs.PostRsDto
import ru.netology.backend.model.dto.rq.RepostRqDto
import ru.netology.backend.model.exception.AccessDeniedException
import ru.netology.backend.model.exception.AlreadyExistException
import ru.netology.backend.model.exception.BadRequestException
import ru.netology.backend.model.exception.NotFoundException
import ru.netology.backend.repository.PostFavoriteRepository
import ru.netology.backend.repository.PostRepository
import ru.netology.backend.repository.PostShareRepository
import ru.netology.backend.service.PostService
import java.net.URL
import java.util.*

class PostServiceImpl(
    private val postRepository: PostRepository,
    private val postFavoriteRepository: PostFavoriteRepository,
    private val postShareRepository: PostShareRepository
) : PostService {
    private val mutex = Mutex()

    override fun getAllAndView(currentUser: User): List<PostRsDto> {
        return postRepository.getAll()
            .onEach { it.views.incrementAndGet() }
            .sortedByDescending { it.createTime }
            .map { this.get(it, currentUser) }
    }

    override fun getAndView(id: UUID, currentUser: User): PostRsDto {
        val post = postRepository.get(id) ?: throw NotFoundException(id)
        post.views.incrementAndGet()
        return get(post, currentUser)
    }

    private fun get(post: Post, currentUser: User): PostRsDto {
        return PostRsDto.fromModel(
            post = post,
            favoriteCount = postFavoriteRepository.getFavoriteCount(post.id),
            favoriteByMe = postFavoriteRepository.isFavoriteByUser(post.id, currentUser.username),
            // TODO Comments Repo
            commentCount = 0L,
            shareCount = postShareRepository.getShareCount(post.id),
            shareByMe = postShareRepository.isShareByUser(post.id, currentUser.username)
        )
    }

    private fun createLinkedEntries(id: UUID) {
        postFavoriteRepository.createEntry(id)
        postShareRepository.createEntry(id)
    }

    private fun deleteLinkedEntries(id: UUID) {
        postFavoriteRepository.deleteEntry(id)
        postShareRepository.deleteEntry(id)
    }

    override fun put(postRqDto: PostRqDto, currentUser: User): PostRsDto {
        val post = Post(
            createdUser = currentUser.username,
            content = postRqDto.content,
            address = postRqDto.address,
            location = postRqDto.location,
            youtubeId = postRqDto.youtubeId,
            commercialContent = if (postRqDto.commercialContent != null) URL(postRqDto.commercialContent) else null
        )

        return put(post, currentUser)
    }

    override fun repost(repostRqDto: RepostRqDto, currentUser: User): PostRsDto {
        val original = UUID.fromString(repostRqDto.original)
        postRepository.get(original) ?: throw BadRequestException("Original Post Not Found")

        val post = Post(
            createdUser = currentUser.username,
            content = repostRqDto.content,
            address = repostRqDto.address,
            location = repostRqDto.location,
            youtubeId = repostRqDto.youtubeId,
            commercialContent = if (repostRqDto.commercialContent != null) URL(repostRqDto.commercialContent) else null,
            original = original
        )

        return put(post, currentUser)
    }

    private fun put(post: Post, currentUser: User): PostRsDto {
        if (postRepository.get(post.id) != null) {
            throw AlreadyExistException(post.id)
        }

        createLinkedEntries(post.id)
        return get(postRepository.put(post), currentUser)
    }

    override suspend fun update(postRqDto: PostRqDto, currentUser: User): PostRsDto {
        if (postRqDto.id == null) {
            throw BadRequestException("Empty id param!")
        }
        val id = UUID.fromString(postRqDto.id)
        val postInRepo = postRepository.get(id) ?: throw NotFoundException(id)
        if (postInRepo.createdUser != currentUser.username) {
            throw AccessDeniedException("Can't change post of another user!")
        }
        mutex.withLock(postInRepo) {
            val post = postInRepo.copy(
                content = postRqDto.content,
                address = postRqDto.address,
                location = postRqDto.location,
                youtubeId = postRqDto.youtubeId,
                commercialContent = if (postRqDto.commercialContent != null) URL(postRqDto.commercialContent) else null
            )

            return get(postRepository.put(post), currentUser)
        }
    }

    override suspend fun delete(id: UUID, currentUser: User) {
        val postInRepo = postRepository.get(id) ?: throw NotFoundException(id)
        if (postInRepo.createdUser != currentUser.username) {
            throw AccessDeniedException("Can't delete post of another user!")
        }
        mutex.withLock(postInRepo) {
            postRepository.delete(id)
            deleteLinkedEntries(id)
        }
    }

    override suspend fun favorite(id: UUID, currentUser: User): PostRsDto {
        val postInRepo = postRepository.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (!postFavoriteRepository.isFavoriteByUser(id, currentUser.username)) {
                postFavoriteRepository.addFavorite(id, currentUser.username)
                return get(postInRepo, currentUser)
            } else {
                throw BadRequestException("Already favorite")
            }
        }
    }

    override suspend fun unfavorite(id: UUID, currentUser: User): PostRsDto {
        val postInRepo = postRepository.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (postFavoriteRepository.isFavoriteByUser(id, currentUser.username)) {
                postFavoriteRepository.removeFavorite(id, currentUser.username)
                return get(postInRepo, currentUser)
            } else {
                throw BadRequestException("Already unfavorite")
            }
        }
    }

    override suspend fun share(id: UUID, currentUser: User): PostRsDto {
        val postInRepo = postRepository.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (!postShareRepository.isShareByUser(id, currentUser.username)) {
                postShareRepository.addShare(id, currentUser.username)
                return get(postInRepo, currentUser)
            } else {
                throw BadRequestException("Already share")
            }
        }
    }
}