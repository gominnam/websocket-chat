package com.booster.controller

import com.booster.config.jwt.JwtService
import com.booster.config.jwt.toUser
import com.booster.dto.UserDTO
import com.booster.enums.ErrorCode
import com.booster.exception.UserException
import com.booster.payload.request.UserRequest
import com.booster.payload.response.AuthResponse
import com.booster.payload.response.UserResponse
import com.booster.services.UserService
import com.booster.util.ApiResponse
import com.booster.util.HttpStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController @Autowired constructor(
        private val userService: UserService,
        private val modelMapper: ModelMapper,
        private val jwtService: JwtService
) {
    val log = KotlinLogging.logger {}

    @PostMapping("/register")
    fun saveUser(@RequestBody request: UserRequest): ApiResponse<AuthResponse>? {
        val userDTO = modelMapper.map(request, UserDTO::class.java)
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
        val (accessToken, refreshToken) = jwtService.issueTokens(userDTO.email!!)
        log.info{"accessToken : $accessToken, refreshToken : $refreshToken"}

        return ApiResponse.Builder<AuthResponse>()
            .status(HttpStatus.OK)
            .message("User created")
            .data(AuthResponse(accessToken, refreshToken))
            .build()
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserRequest): ApiResponse<AuthResponse>? {
        val userDTO = modelMapper.map(request, UserDTO::class.java)
        try{
             userService.login(userDTO)
        } catch (e: UserException){
            return ApiResponse.Builder<AuthResponse>()
                .status(e.getHttpStatus())
                .message(e.message)
                .build()
        }
//        val authResponse = AuthResponse(tokenService.createToken(userDTO))
        val (accessToken, refreshToken) = jwtService.issueTokens(userDTO.email.toString())
        val authResponse = AuthResponse(accessToken, refreshToken)
        return ApiResponse.Builder<AuthResponse>()
            .status(HttpStatus.OK)
            .message("welcome in booster world")
            .data(authResponse)
            .build()
    }

    @GetMapping("/find/{id}")
    fun findUser(@PathVariable id: Long): ApiResponse<UserResponse> {
        val userDTO: UserDTO?
        try{
            userDTO = userService.findById(id)
        } catch (e: UserException){
            return ApiResponse.Builder<UserResponse>()
                .status(e.getHttpStatus())
                .message(e.message)
                .build()
        }
        val userResponse = modelMapper.map(userDTO, UserResponse::class.java)
        return ApiResponse.Builder<UserResponse>()
            .status(HttpStatus.OK)
            .message("find user")
            .data(userResponse)
            .build()
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
