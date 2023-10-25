package com.booster.services

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.exception.ErrorCode
import com.booster.exception.UserException
import com.booster.repositories.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service

@Service
class UserServiceImpl (
    val userRepository: UserRepository,
    val modelMapper: ModelMapper
): UserService {
    override fun createUser(userDTO: UserDTO?): UserDTO? {
        var user = modelMapper.map(userDTO, User::class.java)
        if(userRepository.existsByEmail(user.email)) {
            throw UserException(ErrorCode.USER_ALREADY_EXISTS)
        }
        var savedUser = userRepository.save(user)
        return modelMapper.map(savedUser, UserDTO::class.java)
    }

    override fun findById(id: Long): UserDTO {
        var findUser = userRepository.findById(id)
        return modelMapper.map(findUser, UserDTO::class.java)
    }

    override fun login(userDTO: UserDTO?): UserDTO? {
        var user = modelMapper.map(userDTO, User::class.java)
        var loginUser = userRepository.login(user.email, user.password)
                                        .orElseThrow { throw UserException(ErrorCode.USER_NOT_FOUND) }
        return modelMapper.map(loginUser, UserDTO::class.java)
    }

    override fun deleteById(id: Long) = userRepository.deleteById(id)
}
