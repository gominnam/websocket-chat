package com.booster.services

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.payload.response.UserResponse
import com.booster.repositories.UserRepository
import com.booster.util.ApiResponse
import com.booster.util.HttpStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val modelMapper: ModelMapper
): UserService {
    private val logger = KotlinLogging.logger {}

    override fun createUser(userDTO: UserDTO?): ApiResponse<UserResponse>? {
        var user = modelMapper.map(userDTO, User::class.java)
        if(userRepository.existsByEmail(user.email)) {
            return ApiResponse.Builder<UserResponse>()
                .status(HttpStatus.BAD_REQUEST)
                .message("email already exists")
                .build()
        }

        var savedUser = userRepository.save(user)
        var resultUser = modelMapper.map(savedUser, UserResponse::class.java)
        //todo: log.info("savedUser: ${savedUser}")
        return ApiResponse.Builder<UserResponse>()
            .status(HttpStatus.OK)
            .message("welcome ${savedUser.name}" + " " + "your account has been created")
            .data(resultUser)
            .build()
    }

    override fun findById(id: Long): UserDTO {
        var findUser = userRepository.findById(id)
        return modelMapper.map(findUser, UserDTO::class.java)
    }

    override fun login(userDTO: UserDTO?): UserDTO? {
        var user = modelMapper.map(userDTO, User::class.java)
        var loginUser = userRepository.login(user.email, user.password)
        return modelMapper.map(loginUser, UserDTO::class.java)
    }

    override fun deleteById(id: Long) = userRepository.deleteById(id)

}
