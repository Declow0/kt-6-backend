package ru.netology.backend.repository.impl

import ru.netology.backend.repository.PostShareRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PostShareRepositoryCHM : PostShareRepository {
    private val map = ConcurrentHashMap<UUID, MutableSet<String>>()

    override fun getShareCount(id: UUID) = map[id]?.size?.toLong() ?: 0L

    override fun isShareByUser(id: UUID, username: String): Boolean = map[id]?.contains(username) ?: false

    override fun addShare(id: UUID, username: String) {
        map[id]?.add(username)
    }

    override fun createEntry(id: UUID) {
        map[id] = Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())
    }

    override fun deleteEntry(id: UUID) {
        map.remove(id)
    }
}
