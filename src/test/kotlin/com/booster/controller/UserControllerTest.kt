package com.booster.controller

import com.booster.dto.UserDTO
import com.booster.exception.ErrorCode
import com.booster.exception.UserException
import com.booster.payload.request.UserRequest
import com.booster.payload.response.UserResponse
import com.booster.services.TokenService
import com.booster.services.UserService
import com.booster.util.ApiResponse
import com.booster.util.HttpStatus
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.junit.MockitoJUnitRunner
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


//@ExtendWith(SpringExtension::class)
@RunWith(MockitoJUnitRunner::class)
@WebMvcTest(UserController::class, excludeAutoConfiguration = [SecurityAutoConfiguration::class])
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var mapper: ObjectMapper
    @MockBean
    private lateinit var userService: UserService
    @MockBean
    private lateinit var modelMapper: ModelMapper
    @MockBean
    private lateinit var tokenService: TokenService

    @Test
    @WithMockUser(username = "booster", password = "booster", roles = ["USER"])
    fun `It should get access_token when login successful with given email and password`(){
        val request = UserRequest.Builder().email("booster").password("password").build()
        val userDTO = UserDTO(null, email="booster", "name", "password")

        given(modelMapper.map(request, UserDTO::class.java)).willReturn(userDTO)
        given(userService.login(userDTO)).willReturn(userDTO)
        given(tokenService.createToken(userDTO)).willReturn("access_token")

        //when
        val result = mockMvc.perform(post("/api/user/login")
            .content(mapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf()))

        //then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("welcome in booster world"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value("access_token"))
    }

    @Test
    fun `It should throw exception user not found with given email and password`(){
        val request = UserRequest.Builder().email("not").password("found").build()
        val userDTO = UserDTO(null, email="not", null, "found")

        given(modelMapper.map(request, UserDTO::class.java)).willReturn(userDTO)
        given(userService.login(userDTO)).willAnswer { throw UserException(ErrorCode.USER_NOT_FOUND) }

        //when
        val result = mockMvc.perform(post("/api/user/login")
            .content(mapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf()))

        //then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"))
    }

    @Test
    fun `It should throw exception user already exist with given email and password`(){
        val userRequest = UserRequest(null, "email", "name", "password")
        var userDTO = UserDTO(null, "email", "name", "password")

        given(modelMapper.map(userRequest, UserDTO::class.java)).willReturn(userDTO)
        given(userService.createUser(userDTO)).willAnswer { throw UserException(ErrorCode.USER_ALREADY_EXISTS) }

        //when
        var result = mockMvc.perform(post("/api/user/register")
            .content(mapper.writeValueAsString(userRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf()))

       //then
       result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
           .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User already exists"))
    }

    @Test
    fun `It should get access_token when register user with given email and name and password`(){
        val requestDTO = UserRequest(null, "email", "name", "password")
        val userDTO = UserDTO(null, "email", "name", "password")

        given(modelMapper.map(requestDTO, UserDTO::class.java)).willReturn(userDTO)
        given(userService.createUser(userDTO)).willReturn(userDTO)
        given(tokenService.createToken(userDTO)).willReturn("access_token")

        //when
        val result = mockMvc.perform(post("/api/user/register")
            .content(mapper.writeValueAsString(requestDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf()))

        //then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User created"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value("access_token"))
    }

    @Test
    fun `It should get user when request with given id`(){
        val userDTO = UserDTO(null, "email", "name", null)
        val userResponse = UserResponse(null, "email", "name")

        given(userService.findById(1L)).willReturn(userDTO)
        given(modelMapper.map(userDTO, UserResponse::class.java)).willReturn(userResponse)

        //when
        var result = mockMvc.perform(get("/api/user/find/1"))

        //then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("email"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("name"))
    }

    @Test
    fun `It should throw exception user not found with given id`(){
        given(userService.findById(10000000L)).willAnswer { throw UserException(ErrorCode.USER_NOT_FOUND) }

        //when
        var result = mockMvc.perform(get("/api/user/find/10000000"))

        //then
        result.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"))
    }
}
