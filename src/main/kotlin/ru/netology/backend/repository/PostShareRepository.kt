package ru.netology.backend.repository

import java.util.*

interface PostShareRepository {
    fun getShareCount(id: UUID): Long
    fun isShareByUser(id: UUID, username: String): Boolean

    fun addShare(id: UUID, username: String)

    fun createEntry(id: UUID)
    fun deleteEntry(id: UUID)
}
