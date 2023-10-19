package com.booster.controller

import com.booster.config.toUser
import com.booster.dto.UserDTO
import com.booster.exception.ErrorCode
import com.booster.exception.UserException
import com.booster.payload.request.UserRequest
import com.booster.payload.response.AuthResponse
import com.booster.payload.response.UserResponse
import com.booster.services.HashService
import com.booster.services.TokenService
import com.booster.services.UserService
import com.booster.util.ApiResponse
import com.booster.util.HttpStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController @Autowired constructor(
    private val userService: UserService,
    private val modelMapper: ModelMapper,
    private val tokenService: TokenService,
) {
    val logger = KotlinLogging.logger {}

    @PostMapping("/register")
    fun saveUser(@RequestBody request: UserRequest): ApiResponse<AuthResponse>? {
        var userDTO = modelMapper.map(request, UserDTO::class.java)
        try{
            userService.createUser(userDTO)
        } catch(e: UserException) {
            if(e.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS) {
                return ApiResponse.Builder<AuthResponse>()
                    .status(e.getHttpStatus())
                    .message(e.message)
                    .build()
            }
        }

        var authResponse = AuthResponse(tokenService.createToken(userDTO))
        return ApiResponse.Builder<AuthResponse>()
            .status(HttpStatus.OK)
            .message("user created")
            .data(authResponse)
            .build()
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserRequest): ApiResponse<AuthResponse>? {
        var userDTO = modelMapper.map(request, UserDTO::class.java)
        var loginUser = userService.login(userDTO) ?: return ApiResponse.Builder<AuthResponse>()
                                                                .status(HttpStatus.BAD_REQUEST)
                                                                .message("email or password is incorrect")
                                                                .build()

        var authResponse = AuthResponse(tokenService.createToken(loginUser))
        logger.info{"token: ${authResponse.token}"}
        return ApiResponse.Builder<AuthResponse>()
            .status(HttpStatus.OK)
            .message("welcome ${loginUser.name}")
            .data(authResponse)
            .build()
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

    @GetMapping
    fun someRequest(authentication: Authentication): String {
        val authUser = authentication.toUser()

        return "Hello ${authUser.name}"
    }
}
