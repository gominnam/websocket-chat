package com.booster.services

import com.booster.dto.UserDTO
import org.springframework.stereotype.Component

@Component
interface UserService {
    fun createUser(userDTO: UserDTO?): UserDTO?
    fun findById(id: Long): UserDTO?
    fun findByEmail(email: String?): UserDTO?
    fun login(userDTO: UserDTO?): UserDTO?
    fun deleteById(id: Long)
    fun updateUserName(userDTO: UserDTO, authorization: String): UserDTO?
}