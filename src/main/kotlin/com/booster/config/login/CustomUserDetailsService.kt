package com.booster.config.login

import com.booster.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias ApplicationUser = com.booster.entity.User

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user: ApplicationUser = userRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("Not exist emails") }

        return CustomUserDetails(
            username = user.email,
            password = user.password,
            authorities = listOf(SimpleGrantedAuthority(user.role.name)),
            name = user.name
        )
    }
}
