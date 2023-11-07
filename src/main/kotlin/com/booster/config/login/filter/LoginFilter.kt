package com.booster.config.login.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.util.StreamUtils
import java.io.IOException
import java.nio.charset.StandardCharsets


class LoginFilter(private val objectMapper: ObjectMapper) :
    AbstractAuthenticationProcessingFilter(DEFAULT_LOGIN_PATH_REQUEST_MATCHER) {

    companion object {
        private const val DEFAULT_LOGIN_REQUEST_URL = "/api/user/login"
        private const val HTTP_METHOD = "POST"
        private const val CONTENT_TYPE = "application/json"
        private const val USERNAME_KEY = "email"
        private const val PASSWORD_KEY = "password"
        private val DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD)
    }

    @Throws(AuthenticationException::class, IOException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        if (request.contentType == null || request.contentType != CONTENT_TYPE) {
            throw AuthenticationServiceException("Authentication Content-Type not supported: " + request.contentType)
        }
        val messageBody: String = StreamUtils.copyToString(request.inputStream, StandardCharsets.UTF_8)
        val usernamePasswordMap: Map<*, *> = objectMapper.readValue(messageBody, Map::class.java)
        val email = usernamePasswordMap[USERNAME_KEY]
        val password = usernamePasswordMap[PASSWORD_KEY]
        val authRequest = UsernamePasswordAuthenticationToken(email, password)
        return authenticationManager.authenticate(authRequest)
    }
}