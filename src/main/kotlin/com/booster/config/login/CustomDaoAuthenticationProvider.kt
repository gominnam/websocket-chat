package com.booster.config.login

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails

class CustomDaoAuthenticationProvider : DaoAuthenticationProvider() {

    @Throws(AuthenticationException::class)
    override fun additionalAuthenticationChecks(userDetails: UserDetails, authentication: UsernamePasswordAuthenticationToken) {
        try {
            super.additionalAuthenticationChecks(userDetails, authentication)
        } catch (exception: BadCredentialsException) {
            throw BadCredentialsException("유효하지 않은 정보입니다.", exception)
        }
    }
}