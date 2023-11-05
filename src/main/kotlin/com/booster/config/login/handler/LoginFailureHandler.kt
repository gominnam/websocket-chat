package com.booster.config.login.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import java.io.IOException


class LoginFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    private val log = KotlinLogging.logger{}

    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest?, response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_BAD_REQUEST
        response.characterEncoding = "UTF-8"
        response.contentType = "text/plain;charset=UTF-8"
        response.writer.write("login fail. confirm your email or password")
        log.info { "${"login fail. {}"} ${exception.message}" }
    }
}