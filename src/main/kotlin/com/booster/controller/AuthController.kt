package com.booster.controller

import com.booster.config.jwt.JwtService
import com.booster.dto.AuthDTO
import com.booster.dto.UserDTO
import com.booster.enums.ErrorCode
import com.booster.exception.UserException
import com.booster.payload.request.AuthRequest
import com.booster.payload.request.UserRequest
import com.booster.payload.response.AuthResponse
import com.booster.services.UserService
import com.booster.util.ApiResponse
import com.booster.util.HttpStatus
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("api/oauth2")
class AuthController @Autowired constructor(
    private val userService: UserService,
    private val modelMapper: ModelMapper,
    private val jwtService: JwtService
) {
    @PostMapping("/signup")
    // params: request, header
    fun signup(@RequestBody request: AuthRequest,
               @RequestHeader("Authorization") authorization: String): ApiResponse<AuthResponse>? {
        val authDTO = modelMapper.map(request, AuthDTO::class.java)

        try{
            userService.updateUser(authDTO, authorization)
        } catch(e: UserException) {
            if(e.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS) {
                return ApiResponse.Builder<AuthResponse>()
                    .status(e.getHttpStatus())
                    .message(e.message)
                    .build()
            }
        }
        val (accessToken, refreshToken) = jwtService.issueTokens(userDTO.email!!)

        return ApiResponse.Builder<AuthResponse>()
            .status(HttpStatus.OK)
            .message("User created")
            .data(AuthResponse(accessToken, refreshToken))
            .build()
    }
}