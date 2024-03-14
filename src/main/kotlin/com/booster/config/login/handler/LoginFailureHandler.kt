package com.booster.config.login.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import java.io.IOException


class LoginFailureHandler : SimpleUrlAuthenticationFailureHandler() {
    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest?, response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.characterEncoding = "UTF-8"
        response.contentType = "application/json"
        response.writer.write("{\"error\": \"${exception.message}\"}")
    }
}