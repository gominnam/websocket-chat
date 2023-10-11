package com.booster.controller

import com.booster.entity.User
import com.booster.repositories.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ContextConfiguration(locations= ["classpath:application.properties"]) todo: create test configuration file
class UserControllerTest @Autowired constructor(
    val mockMvc: MockMvc, val mapper: ObjectMapper, val userRepository: UserRepository
){
    var user: User? = null

    @BeforeAll
    fun setup() {
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
        var curUser = generateRandomUser()

        //save
        var insertResult = mockMvc.perform(post("/api/user/save")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(curUser)))
            .andExpect(status().isOk)
            .andReturn()

        curUser = mapper.readValue(
            insertResult.response.contentAsString,
            User::class.java
        )

        //find
        mockMvc.perform(post("/api/user/find/${curUser.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(curUser.email))
            .andExpect(jsonPath("$.name").value(curUser.name))

        //delete
        mockMvc.perform(post("/api/user/delete/${curUser.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("Login Test")
    fun login(){
        mockMvc.perform(post("/api/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(user)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(user?.email))
            .andExpect(jsonPath("$.name").value(user?.name))
    }
}
