package com.booster.config.login.handler

import com.booster.config.jwt.JwtService
import com.booster.config.login.CustomUserDetails
import com.booster.entity.User
import com.booster.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

class LoginSuccessHandler(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : SimpleUrlAuthenticationSuccessHandler() {

    private val log = KotlinLogging.logger {}

    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse,
        authentication: Authentication
    ) {
        val name = extractUsername(authentication)
        val email = extractEmail(authentication)
        val accessToken = jwtService.createAccessToken(name, email)
        val refreshToken = jwtService.createRefreshToken()

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken)

        userRepository.findByEmail(email).ifPresent { user: User ->
            user.refreshToken = refreshToken
            log.info { "user refreshToken: $refreshToken" }
            log.info { "user refreshToken: ${user.refreshToken}"}
            userRepository.saveAndFlush(user)
        }
    }

    private fun extractUsername(authentication: Authentication): String {
        val userDetails = authentication.principal as CustomUserDetails
        return userDetails.getName()
    }

    private fun extractEmail(authentication: Authentication): String {
        val userDetails = authentication.principal as CustomUserDetails
        return userDetails.username
    }
}
