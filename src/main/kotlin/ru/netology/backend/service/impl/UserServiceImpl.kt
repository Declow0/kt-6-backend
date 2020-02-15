package ru.netology.backend.service.impl

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.security.crypto.password.PasswordEncoder
import ru.netology.backend.model.User
import ru.netology.backend.model.exception.AlreadyExistException
import ru.netology.backend.model.exception.NotFoundException
import ru.netology.backend.repository.UserRepository
import ru.netology.backend.service.UserService

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {
    private val mutex = Mutex()

    override fun get(username: String): User =
        userRepository.get(username) ?: throw NotFoundException("Not Found User with username: $username")


    override suspend fun put(user: User): User {
        mutex.withLock {
            val userInRepo = userRepository.get(user.username)
            if (userInRepo != null) {
                throw AlreadyExistException("User with username: ${userInRepo.username} already exist!")
            }

            return userRepository.put(
                user.copy(password = passwordEncoder.encode(user.password))
            )
        }
    }
}