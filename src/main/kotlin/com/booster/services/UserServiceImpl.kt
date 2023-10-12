package com.booster.services

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.repositories.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val modelMapper: ModelMapper
): UserService {
    override fun createUser(userDTO: UserDTO?): UserDTO? {
        var user = modelMapper.map(userDTO, User::class.java)
        var savedUser = userRepository.save(user)
        //todo: log.info("savedUser: ${savedUser}")
        return modelMapper.map(savedUser, UserDTO::class.java)
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
