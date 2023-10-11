package com.booster.services

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.repositories.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("userService")
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val modelMapper: ModelMapper
) {
    fun save(userDTO: UserDTO): UserDTO {
        var user = modelMapper.map(userDTO, User::class.java)
        return modelMapper.map(userRepository.save(user), UserDTO.Builder::class.java).build()
    }
    fun findById(id: Long) = userRepository.findById(id)
    fun findAll() = userRepository.findAll()
    fun deleteById(id: Long) = userRepository.deleteById(id)
    fun login(userDTO: UserDTO): UserDTO {
        var user = modelMapper.map(userDTO, User::class.java)
        return modelMapper.map(userRepository.login(user.email, user.password), UserDTO.Builder::class.java).build()
    }

}