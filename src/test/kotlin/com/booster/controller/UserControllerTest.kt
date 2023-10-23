package com.booster.controller

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.payload.request.UserRequest
import com.booster.payload.response.UserResponse
import com.booster.repositories.UserRepository
import com.booster.services.TokenService
import com.booster.util.ApiResponse
import com.booster.util.HttpStatus
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.*
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val mapper: ObjectMapper,
    val userRepository: UserRepository,
    val modelMapper: ModelMapper,
    private val tokenService: TokenService,
){
    var user: User? = null
    val logger = KotlinLogging.logger {}

    @BeforeAll
    fun setup() {
        userRepository.save(generateRandomUser())
        user = userRepository.getReferenceById(1L)
    }
    companion object {
        @BeforeAll
        fun generateRandomUser(): User {
            val randomNumber = Random().nextInt(10000)
            val username = "user${randomNumber}"
            val email = "user${randomNumber}@booster.com"
            return User(null, email, username, "password")
        }
    }

    @Test
    @DisplayName("CRUD Test")
    fun crud_User() {
        var request = modelMapper.map(generateRandomUser(), UserRequest::class.java)

        //save
        var insertResult = mockMvc.perform(post("/api/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andReturn()

        var resultUser = mapper.readValue(
            insertResult.response.contentAsString,
            User::class.java
        )

        //find
        mockMvc.perform(post("/api/user/find/${resultUser.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(resultUser.email))
            .andExpect(jsonPath("$.name").value(resultUser.name))

        //delete
        mockMvc.perform(post("/api/user/delete/${resultUser.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    @Test
    fun httpStatusTest(){
        var test = ApiResponse.Builder<UserResponse>()
            .status(HttpStatus.BAD_REQUEST)
            .message("email already exists")
            .build()

        logger.info{ "test value : ${test.status.toString()}" }
    }

    @Test
    @DisplayName("findByEmail Test")
    fun findByEmail(){
        var request = modelMapper.map(user, UserRequest::class.java)
        logger.info{ "request: request: $request" }

        mockMvc.perform(post("/api/user/find/${user?.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(user?.email))
            .andExpect(jsonPath("$.name").value(user?.name))
    }

    @Test
    @DisplayName("Login Test")
    fun login(){
        var request = modelMapper.map(user, UserRequest::class.java)

        mockMvc.perform(post("/api/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(user?.email))
            .andExpect(jsonPath("$.name").value(user?.name))
    }

//    fun login(@RequestBody request: UserRequest): ApiResponse<AuthResponse>? {
//        var userDTO = modelMapper.map(request, UserDTO::class.java)
//        var loginUser = userService.login(userDTO) ?: return ApiResponse.Builder<AuthResponse>()
//            .status(HttpStatus.BAD_REQUEST)
//            .message("email or password is incorrect")
//            .build()
//
//        var authResponse = AuthResponse(tokenService.createToken(loginUser))
//        logger.info{"token: ${authResponse.token}"}
//        return ApiResponse.Builder<AuthResponse>()
//            .status(HttpStatus.OK)
//            .message("welcome ${loginUser.name}")
//            .data(authResponse)
//            .build()
//    }
}
