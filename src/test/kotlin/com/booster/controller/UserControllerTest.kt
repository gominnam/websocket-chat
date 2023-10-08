package com.booster.controller

import com.booster.entity.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest @Autowired constructor(val mockMvc: MockMvc, val mapper: ObjectMapper){

    @Test
    fun saveUser() {
        var user = User(null, "kosok03@naver.com", "고민준", "password")
        mockMvc.perform(post("/user/save")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(user)))
            .andExpect(status().isOk)
//            .andExpect(jsonPath("\$.id").exists())
//            .andExpect(jsonPath("\$.email").value("kosok03@naver.com"))
    }
    @Test
    fun getAllUsers() {

    }
}