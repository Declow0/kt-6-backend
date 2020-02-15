package ru.netology.backend.service.impl

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.backend.model.Post
import ru.netology.backend.model.User
import ru.netology.backend.model.dto.PostRqDto
import ru.netology.backend.model.dto.PostRsDto
import ru.netology.backend.model.dto.RepostRqDto
import ru.netology.backend.model.exception.AccessDeniedException
import ru.netology.backend.model.exception.AlreadyExistException
import ru.netology.backend.model.exception.BadRequestException
import ru.netology.backend.model.exception.NotFoundException
import ru.netology.backend.repository.PostRepository
import ru.netology.backend.service.PostService
import java.net.URL
import java.util.*

class PostServiceImpl(private val postRepository: PostRepository) : PostService {
    private val mutex = Mutex()

    override fun getAllAndView(): List<PostRsDto> {
        return postRepository.getAll()
            .onEach { it.views.incrementAndGet() }
            .sortedByDescending { it.createTime }
            .map(PostRsDto.Companion::fromModel)
    }

    override fun getAndView(id: UUID): PostRsDto {
        val post = postRepository.get(id) ?: throw NotFoundException(id)
        post.views.incrementAndGet()
        return PostRsDto.fromModel(post)
    }

    override fun get(id: UUID): PostRsDto {
        val post = postRepository.get(id) ?: throw NotFoundException(id)
        return PostRsDto.fromModel(post)
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

        val postInRepo = postRepository.get(post.id)
        if (postInRepo != null) {
            throw AlreadyExistException(postInRepo.id)
        }
        return PostRsDto.fromModel(postRepository.put(post))
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

            return PostRsDto.fromModel(postRepository.put(post))
        }
    }

    override suspend fun delete(id: UUID, currentUser: User) {
        val postInRepo = postRepository.get(id) ?: throw NotFoundException(id)
        if (postInRepo.createdUser != currentUser.username) {
            throw AccessDeniedException("Can't delete post of another user!")
        }
        mutex.withLock(postInRepo) {
            postRepository.delete(id)
        }
    }

    override suspend fun favorite(id: UUID): PostRsDto {
        val postInRepo = postRepository.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (!postInRepo.favoriteByMe) {
                val post: Post = postInRepo.copy(
                    favorite = postInRepo.favorite + 1,
                    favoriteByMe = true
                )
                return PostRsDto.fromModel(postRepository.put(post))
            } else {
                throw BadRequestException("Already favorite")
            }
        }
    }

    override suspend fun unfavorite(id: UUID): PostRsDto {
        val postInRepo = postRepository.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (postInRepo.favoriteByMe) {
                val post: Post = postInRepo.copy(
                    favorite = postInRepo.favorite - 1,
                    favoriteByMe = false
                )
                return PostRsDto.fromModel(postRepository.put(post))
            } else {
                throw BadRequestException("Already unfavorite")
            }
        }
    }

    override suspend fun share(id: UUID): PostRsDto {
        val postInRepo = postRepository.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (!postInRepo.shareByMe) {
                val post: Post = postInRepo.copy(
                    share = postInRepo.share + 1,
                    shareByMe = true
                )
                return PostRsDto.fromModel(postRepository.put(post))
            } else {
                throw BadRequestException("Already share")
            }
        }
    }

    override suspend fun repost(repostRqDto: RepostRqDto, currentUser: User): PostRsDto {
        val original = UUID.fromString(repostRqDto.original)
        try {
            get(original)
        } catch (e: NotFoundException) {
            throw BadRequestException("Original Post Not Found")
        }

        val post = Post(
            createdUser = currentUser.username,
            content = repostRqDto.content,
            address = repostRqDto.address,
            location = repostRqDto.location,
            youtubeId = repostRqDto.youtubeId,
            commercialContent = if (repostRqDto.commercialContent != null) URL(repostRqDto.commercialContent) else null,
            original = original
        )

        if (postRepository.get(post.id) != null) {
            throw AlreadyExistException(post.id)
        }

        return PostRsDto.fromModel(
            postRepository.put(post)
        )
    }
}