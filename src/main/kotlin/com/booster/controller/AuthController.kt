package com.booster.controller

import com.booster.config.jwt.JwtService
import com.booster.dto.UserDTO
import com.booster.enums.ErrorCode
import com.booster.exception.AuthException
import com.booster.exception.CommonException
import com.booster.exception.UserException
import com.booster.payload.request.AuthRequest
import com.booster.payload.response.UserResponse
import com.booster.services.UserService
import com.booster.util.ApiResponse
import com.booster.util.HttpStatus
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/oauth2")
class AuthController @Autowired constructor(
    private val userService: UserService,
    private val modelMapper: ModelMapper,
    private val jwtService: JwtService
) {
    @PostMapping("/signup")
    fun signup(@RequestBody request: AuthRequest,
               @RequestHeader("Authorization") authorization: String): ApiResponse<UserResponse>? {
        val userDTO = modelMapper.map(request, UserDTO::class.java)
        var userResponse: UserResponse? = null
        try{
            val updatedUserDTO = userService.updateUserName(userDTO, authorization)!!
            userResponse = modelMapper.map(updatedUserDTO, UserResponse::class.java)
        } catch(e: AuthException){
            if(e.getErrorCode() == ErrorCode.TOKEN_INVALID) {
                return ApiResponse.Builder<UserResponse>()
                    .status(e.getHttpStatus())
                    .message(e.message)
                    .build()
            }
        } catch(e: UserException) {
            if(e.getErrorCode() == ErrorCode.USER_ALREADY_EXISTS) {
                return ApiResponse.Builder<UserResponse>()
                    .status(e.getHttpStatus())
                    .message(e.message)
                    .build()
            }
        } catch(e: CommonException){
            return ApiResponse.Builder<UserResponse>()
                .status(e.getHttpStatus())
                .message(e.message)
                .build()
        }

        return  ApiResponse.Builder<UserResponse>()
            .status(HttpStatus.OK)
            .message("Successfully updated user")
            .data(userResponse)
            .build()
    }
}