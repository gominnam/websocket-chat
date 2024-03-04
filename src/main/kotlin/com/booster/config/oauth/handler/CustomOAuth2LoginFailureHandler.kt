package com.booster.config.oauth.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler

class CustomOAuth2LoginFailureHandler : AuthenticationFailureHandler {

    companion object {
        private val log = LoggerFactory.getLogger(CustomOAuth2LoginFailureHandler::class.java)
    }

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        response?.status = HttpServletResponse.SC_BAD_REQUEST
        //response?.writer?.write("social login failure!!")
        response?.sendRedirect("/error?message=youNeedToLogIn")
        log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", exception?.message)
    }
}