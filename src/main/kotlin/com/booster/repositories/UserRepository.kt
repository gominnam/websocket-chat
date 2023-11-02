package com.booster.repositories

import com.booster.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository: JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.password = ?2")
    fun login(email: String, password: String): Optional<User>

    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}
