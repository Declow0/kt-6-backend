package ru.netology.backend.repository.impl

import ru.netology.backend.repository.PostFavoriteRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PostFavoriteRepositoryCHM : PostFavoriteRepository {
    private val map = ConcurrentHashMap<UUID, MutableSet<String>>()

    override fun getFavoriteCount(id: UUID) = map[id]?.size?.toLong() ?: 0L

    override fun isFavoriteByUser(id: UUID, username: String) = map[id]?.contains(username) ?: false

    override fun addFavorite(id: UUID, username: String) {
        map[id]?.add(username)
    }

    override fun removeFavorite(id: UUID, username: String) {
        map[id]?.remove(username)
    }

    override fun createEntry(id: UUID) {
        map[id] = Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())
    }

    override fun deleteEntry(id: UUID) {
        map.remove(id)
    }
}