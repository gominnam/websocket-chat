package com.booster.controller

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.payload.request.UserRequest
import com.booster.payload.response.UserResponse
import com.booster.services.UserService
import com.booster.services.UserServiceImpl
import com.booster.util.ApiResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/user")
class UserController @Autowired constructor(
    private val userService: UserService,
    private val modelMapper: ModelMapper
) {
    val logger = KotlinLogging.logger {}
    @PostMapping("/save")
    fun saveUser(@RequestBody request: UserRequest): ApiResponse<UserResponse>? {
        var userDTO = modelMapper.map(request, UserDTO::class.java)
        var t = userService.createUser(userDTO)
        logger.info{"HttpStatus in controller : $t"}
        return t
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserRequest): ResponseEntity<UserResponse> {
        var userDTO = modelMapper.map(request, UserDTO::class.java)
        var loginUser = userService.login(userDTO)
        var userResponse = modelMapper.map(loginUser, UserResponse::class.java)
        return ResponseEntity(userResponse, HttpStatus.OK)
    }

    @PostMapping("/find/{id}")
    fun findUser(@PathVariable id: Long): ResponseEntity<UserResponse> {
        var findUser = modelMapper.map(userService.findById(id), UserResponse::class.java)
        return ResponseEntity.ok(findUser)
    }

    @PostMapping("/delete/{id}")
    fun deleteUser(@PathVariable id: Long): String {
        userService.deleteById(id)
        return "delete success"
    }
}
