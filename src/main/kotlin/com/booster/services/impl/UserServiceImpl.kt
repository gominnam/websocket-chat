package com.booster.services.impl

import com.booster.config.jwt.JwtService
import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.enums.ErrorCode
import com.booster.enums.Role
import com.booster.exception.AuthException
import com.booster.exception.CommonException
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
    val passwordEncoder: PasswordEncoder,
    val jwtService: JwtService,
): UserService {

    override fun createUser(userDTO: UserDTO?): UserDTO? {
        val user = modelMapper.map(userDTO, User::class.java)
        if(userRepository.existsByEmail(user.email)) {
            throw UserException(ErrorCode.USER_ALREADY_EXISTS)
        }
        user.password = passwordEncoder.encode(user.password)
        val savedUser = userRepository.save(user)
        return modelMapper.map(savedUser, UserDTO::class.java)
    }

    override fun findById(id: Long): UserDTO {
        val findUser = userRepository.findById(id)
                                        .orElseThrow{ throw UserException(ErrorCode.USER_NOT_FOUND)}
        return modelMapper.map(findUser, UserDTO::class.java)
    }

    override fun login(userDTO: UserDTO?): UserDTO? {
        val user = modelMapper.map(userDTO, User::class.java)
        val loginUser = userRepository.findByEmail(user.email)
                                        .orElseThrow { throw UserException(ErrorCode.USER_NOT_FOUND) }

        if(!passwordEncoder.matches(user.password, loginUser.password)) {
            throw UserException(ErrorCode.USER_PASSWORD_NOT_MATCH)
        }

        return modelMapper.map(loginUser, UserDTO::class.java)
    }

    override fun updateUserName(userDTO: UserDTO, authorization: String): UserDTO {
        var email: String?
        try {
            email = jwtService.extractEmail(authorization)
            userDTO.email = email
            userDTO.password = ""
        } catch(e: AuthException) {
            throw AuthException(ErrorCode.TOKEN_INVALID)
        }
        val user = modelMapper.map(userDTO, User::class.java)
        user.role = Role.USER
        val updatedCount = userRepository.updateUserNameByEmail(user.email, user.name, user.role)
        if(updatedCount == 0) {
            throw CommonException(ErrorCode.SERVER_ERROR)
        }
        val updatedUser = userRepository.findByEmail(user.email)
                                        .orElseThrow { throw UserException(ErrorCode.USER_NOT_FOUND) }
        return modelMapper.map(updatedUser, UserDTO::class.java)
    }

    override fun deleteById(id: Long) = userRepository.deleteById(id)

    override fun findByEmail(email: String?): UserDTO? {
        if(email == null) {
            throw UserException(ErrorCode.USER_NOT_FOUND)
        }
        val user = userRepository.findByEmail(email)
                                .orElseThrow { throw UserException(ErrorCode.USER_NOT_FOUND) }
        return modelMapper.map(user, UserDTO::class.java)
    }
}
