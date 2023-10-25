package com.booster.controller

import com.booster.dto.UserDTO
import com.booster.payload.request.UserRequest
import com.booster.payload.response.AuthResponse
import com.booster.services.TokenService
import com.booster.services.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.junit.MockitoJUnitRunner
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post


//@ExtendWith(SpringExtension::class)
@RunWith(MockitoJUnitRunner::class)
@WebMvcTest(UserController::class)
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
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("welcome in booster world"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value("access_token"))
    }
}
