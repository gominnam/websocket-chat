package com.booster.services

import com.booster.entity.User
import com.booster.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository
) {
    fun save(user: User): User {
        return userRepository.save(user)
    }

    fun findById(id: Long) = userRepository.findById(id)
    fun findAll() = userRepository.findAll()
}
