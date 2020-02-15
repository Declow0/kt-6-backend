package ru.netology.backend.repository

import java.util.*

interface PostFavoriteRepository {
    fun getFavoriteCount(id: UUID): Long
    fun isFavoriteByUser(id: UUID, username: String): Boolean

    fun addFavorite(id: UUID, username: String)
    fun removeFavorite(id: UUID, username: String)

    fun createEntry(id: UUID)
    fun deleteEntry(id: UUID)
}