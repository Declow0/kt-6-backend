package ru.netology.backend.repository.impl

import ru.netology.backend.model.User
import ru.netology.backend.repository.UserRepository
import java.util.concurrent.ConcurrentHashMap

class UserRepositoryConcurrentHashMap : UserRepository {
    private val map = ConcurrentHashMap<String, User>()

    override fun get(username: String): User? = map[username]

    override fun put(post: User): User {
        map[post.username] = post
        return post
    }
}