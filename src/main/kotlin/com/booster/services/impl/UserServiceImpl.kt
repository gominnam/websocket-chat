package com.booster.services.impl

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.enums.ErrorCode
import com.booster.exception.UserException
import com.booster.repositories.UserRepository
import com.booster.services.UserService
import org.modelmapper.ModelMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl (
    val userRepository: UserRepository,
    val modelMapper: ModelMapper,
    val passwordEncoder: PasswordEncoder
): UserService {

    override fun createUser(userDTO: UserDTO?): UserDTO? {
        var user = modelMapper.map(userDTO, User::class.java)
        if(userRepository.existsByEmail(user.email)) {
            throw UserException(ErrorCode.USER_ALREADY_EXISTS)
        }
        user.password = passwordEncoder.encode(user.password)
        var savedUser = userRepository.save(user)
        return modelMapper.map(savedUser, UserDTO::class.java)
    }

    override fun findById(id: Long): UserDTO {
        var findUser = userRepository.findById(id)
                                        .orElseThrow{ throw UserException(ErrorCode.USER_NOT_FOUND)}
        return modelMapper.map(findUser, UserDTO::class.java)
    }

    override fun login(userDTO: UserDTO?): UserDTO? {
        var user = modelMapper.map(userDTO, User::class.java)
        var loginUser = userRepository.findByEmail(user.email)
                                        .orElseThrow { throw UserException(ErrorCode.USER_NOT_FOUND) }

        if(!passwordEncoder.matches(user.password, loginUser.password)) {
            throw UserException(ErrorCode.USER_PASSWORD_NOT_MATCH)
        }

        return modelMapper.map(loginUser, UserDTO::class.java)
    }

    override fun deleteById(id: Long) = userRepository.deleteById(id)
}
