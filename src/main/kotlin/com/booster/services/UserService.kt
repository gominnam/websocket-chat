package com.booster.services

import com.booster.dto.UserDTO
import com.booster.entity.User
import org.springframework.stereotype.Component

@Component
interface UserService {
    fun createUser(userDTO: UserDTO?): UserDTO?
    fun findById(id: Long): UserDTO?
    fun login(userDTO: UserDTO?): UserDTO?
    fun deleteById(id: Long)
}