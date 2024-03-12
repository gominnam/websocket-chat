package com.booster.config

import org.springframework.stereotype.Component

@Component
class SecurityConfigProperties {
    companion object {
        val NO_CHECK_URLS = setOf(
            "/",
            "/components/**",
            "/main",
            "/signup",
            "/error",
            "/favicon.ico",
            "/css/**",
            "/images/**",
            "/js/**",
            "/webjars/**",
            "/oauth2/**",
            "/login/**",
            "/api/user/register"
        )
    }
}
