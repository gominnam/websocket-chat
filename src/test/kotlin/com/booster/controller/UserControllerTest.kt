package com.booster.controller

import com.booster.entity.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
class UserControllerTest @Autowired constructor(val mockMvc: MockMvc, val mapper: ObjectMapper){

//    var user = User(null, "test@naver.com", "고민준", "password")

    companion object {
        @BeforeAll
        fun generateRandomUser(): User {
            var random = Random()
            val randomNumber = random.nextInt(10000)
            val username = "user${randomNumber}"
            val email = "user${randomNumber}@booster.com"
            return User(null, email, username, "password")
        }
    }

    @Test
    fun crud_User() {
        var user = generateRandomUser()

        //save
        var insertResult = mockMvc.perform(post("/user/save")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(user)))
            .andExpect(status().isOk)
            .andReturn()

        user = mapper.readValue(
            insertResult.response.contentAsString,
            User::class.java
        )

        //find
        mockMvc.perform(post("/user/find/${user.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(user.email))
            .andExpect(jsonPath("$.name").value(user.name))

        //delete
        mockMvc.perform(post("/user/delete/${user.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }
}
