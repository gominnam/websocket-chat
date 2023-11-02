package com.booster.exception

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.access.AccessDeniedException
import java.io.IOException


class UserForbiddenErrorHandler : AccessDeniedHandler {
    private val objectMapper: ObjectMapper = ObjectMapper()

    @Throws(IOException::class)
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        ex: AccessDeniedException?
    ) {
        response?.contentType = "application/json"
        response?.status = HttpServletResponse.SC_FORBIDDEN
        response?.outputStream?.println(objectMapper.writeValueAsString(CommonException.forbidden()))
    }
}