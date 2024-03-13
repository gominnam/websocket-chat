package com.booster.config.oauth.handler

import com.booster.config.jwt.JwtService
import com.booster.config.oauth.CustomOAuth2User
import com.booster.enums.Role
import com.booster.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.io.IOException


class CustomOAuth2LoginSuccessHandler(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : AuthenticationSuccessHandler {

    private val log = KotlinLogging.logger {}

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        try {
            val oAuth2User: CustomOAuth2User = authentication.principal as CustomOAuth2User
            if (oAuth2User.role === Role.GUEST) {
                val accessToken = jwtService.createAccessToken(oAuth2User.email)
                response.sendRedirect("/signup?accessToken=$accessToken")
                log.info { "User Role is GUEST. Redirect to /signup" }
            } else {
                val accessToken = jwtService.createAccessToken(oAuth2User.email)
                val refreshToken = jwtService.createRefreshToken()
                jwtService.updateRefreshToken(oAuth2User.email, refreshToken)
                response.sendRedirect("/chat?accessToken=$accessToken&refreshToken=$refreshToken")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}