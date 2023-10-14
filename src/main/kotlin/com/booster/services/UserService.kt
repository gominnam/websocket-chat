package com.booster.services

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.payload.response.UserResponse
import com.booster.util.ApiResponse
import org.springframework.stereotype.Component

@Component
interface UserService {
    fun createUser(userDTO: UserDTO?): ApiResponse<UserResponse>?
    fun findById(id: Long): UserDTO?
    fun login(userDTO: UserDTO?): UserDTO?
    fun deleteById(id: Long)
}