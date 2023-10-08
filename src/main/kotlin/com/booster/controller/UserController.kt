package com.booster.controller

import com.booster.entity.User
import com.booster.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(private val userService: UserService) {

    @GetMapping("/test")
    fun test(): String {
        return "test"
    }

    @GetMapping("/all")
    fun getAllUsers(): List<User> {
        return userService.findAll()
    }

    @PostMapping("/save")
    fun saveUser(@RequestBody user: User): User {
        return userService.save(user)
    }

    @GetMapping("/find/{id}")
    fun findUser(@PathVariable id: Long): Optional<User> {
        return userService.findById(id)
    }
}
