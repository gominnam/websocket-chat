package com.booster.config.login

import com.booster.enums.ErrorCode
import com.booster.exception.UserException
import com.booster.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class LoginService(val userRepository: UserRepository) : UserDetailsService {
    @Throws(UserException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            .orElseThrow { throw UserException(ErrorCode.USER_NOT_FOUND) }

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.email)
            .password(user.password)
            .roles(user.role.name)
            .build()
    }
}