package ru.netology.backend.service.impl

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.netology.backend.model.Post
import ru.netology.backend.model.dto.PostRqDto
import ru.netology.backend.model.dto.PostRsDto
import ru.netology.backend.model.dto.RepostRqDto
import ru.netology.backend.model.exception.AlreadyExistException
import ru.netology.backend.model.exception.BadRequestException
import ru.netology.backend.model.exception.NotFoundException
import ru.netology.backend.repository.PostRepository
import ru.netology.backend.service.PostService
import java.net.URL
import java.util.*

class PostServiceImpl(val repo: PostRepository) : PostService {
    private val mutex = Mutex()

    override fun getAllAndView(): List<PostRsDto> {
        return repo.getAll()
            .onEach { it.views.incrementAndGet() }
            .sortedByDescending { it.createTime }
            .map(PostRsDto.Companion::fromModel)
    }

    override fun getAndView(id: UUID): PostRsDto {
        val post = repo.get(id) ?: throw NotFoundException(id)
        post.views.incrementAndGet()
        return PostRsDto.fromModel(post)
    }

    override fun get(id: UUID): PostRsDto {
        val post = repo.get(id) ?: throw NotFoundException(id)
        return PostRsDto.fromModel(post)
    }

    override fun put(postRqDto: PostRqDto): PostRsDto {
        val post = Post(
            createdUser = postRqDto.createdUser,
            content = postRqDto.content,
            address = postRqDto.address,
            location = postRqDto.location,
            youtubeId = postRqDto.youtubeId,
            commercialContent = if (postRqDto.commercialContent != null) URL(postRqDto.commercialContent) else null
        )

        val postInRepo = repo.get(post.id)
        if (postInRepo != null) {
            throw AlreadyExistException(postInRepo.id)
        }
        return PostRsDto.fromModel(repo.put(post))
    }

    override suspend fun delete(id: UUID) {
        val postInRepo = repo.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            repo.delete(id)
        }
    }

    override suspend fun favorite(id: UUID): PostRsDto {
        val postInRepo = repo.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (!postInRepo.favoriteByMe) {
                val post: Post = postInRepo.copy(
                    favorite = postInRepo.favorite + 1,
                    favoriteByMe = true
                )
                return PostRsDto.fromModel(repo.put(post))
            } else {
                throw BadRequestException("Already favorite")
            }
        }
    }

    override suspend fun unfavorite(id: UUID): PostRsDto {
        val postInRepo = repo.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (postInRepo.favoriteByMe) {
                val post: Post = postInRepo.copy(
                    favorite = postInRepo.favorite - 1,
                    favoriteByMe = false
                )
                return PostRsDto.fromModel(repo.put(post))
            } else {
                throw BadRequestException("Already unfavorite")
            }
        }
    }

    override suspend fun share(id: UUID): PostRsDto {
        val postInRepo = repo.get(id) ?: throw NotFoundException(id)
        mutex.withLock(postInRepo) {
            if (!postInRepo.shareByMe) {
                val post: Post = postInRepo.copy(
                    share = postInRepo.share + 1,
                    shareByMe = true
                )
                return PostRsDto.fromModel(repo.put(post))
            } else {
                throw BadRequestException("Already share")
            }
        }
    }

    override suspend fun repost(repostRqDto: RepostRqDto): PostRsDto {
        val original = UUID.fromString(repostRqDto.original)
        try {
            get(original)
        } catch (e: NotFoundException) {
            throw BadRequestException("Original Post Not Found")
        }

        val post = Post(
            createdUser = repostRqDto.createdUser,
            content = repostRqDto.content,
            address = repostRqDto.address,
            location = repostRqDto.location,
            youtubeId = repostRqDto.youtubeId,
            commercialContent = if (repostRqDto.commercialContent != null) URL(repostRqDto.commercialContent) else null,
            original = original
        )

        if (repo.get(post.id) != null) {
            throw AlreadyExistException(post.id)
        }

        return PostRsDto.fromModel(
            repo.put(post)
        )
    }
}