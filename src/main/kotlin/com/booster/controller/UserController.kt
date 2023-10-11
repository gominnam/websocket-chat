package com.booster.controller

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/user")
class UserController @Autowired constructor(private val userService: UserService) {
    @GetMapping("/all")
    fun getAllUsers(): List<User> {
        return userService.findAll()
    }

    @PostMapping("/save")
    fun saveUser(@RequestBody userDTO: UserDTO): ResponseEntity<UserDTO> {
        return ResponseEntity(userService.save(userDTO), HttpStatus.OK)
    }

    @PostMapping("/find/{id}")
    fun findUser(@PathVariable id: Long): Optional<User> {
        return userService.findById(id)
    }

    @PostMapping("/delete/{id}")
    fun deleteUser(@PathVariable id: Long): String {
        userService.deleteById(id)
        return "delete success"
    }

    @PostMapping("/login")
    fun login(@RequestBody userDTO: UserDTO): ResponseEntity<UserDTO> {
        return ResponseEntity(userService.login(userDTO), HttpStatus.OK)
    }

}
