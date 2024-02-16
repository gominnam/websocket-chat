package com.booster.config.jwt

import com.booster.entity.User
import com.booster.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


class JwtTokenAuthenticationFilter(private val jwtService: JwtService?, private val userRepository: UserRepository?)
    : OncePerRequestFilter() {

    private val authoritiesMapper: GrantedAuthoritiesMapper = NullAuthoritiesMapper()

    private val log = KotlinLogging.logger {}

    companion object {
        val NO_CHECK_URL_PATTERNS = listOf(
            "^/$",
            "^/favicon.ico$",
            "^/css/.*",
            "^/images/.*",
            "^/js/.*",
            "^/api/user/login$",
            "^/api/user/register$"
        ).map { it.toRegex() }
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.info { "JwtTokenAuthenticationFilter.doFilterInternal()" }
        log.info { "requestURI: " + request.requestURI }
        if (NO_CHECK_URL_PATTERNS.any { it.matches(request.requestURI) }) {
            filterChain.doFilter(request, response)
            return
        }

        val refreshToken = jwtService?.extractRefreshToken(request)
            ?.takeIf { jwtService.isTokenValid(it) }

        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken)
            return
        }

        checkAccessTokenAndAuthentication(request, response, filterChain)
    }

    private fun checkRefreshTokenAndReIssueAccessToken(response: HttpServletResponse?, refreshToken: String?) {
        userRepository?.findByRefreshToken(refreshToken)
            ?.ifPresent { user ->
                val reIssuedRefreshToken = reissueRefreshToken(user)
                jwtService!!.sendAccessAndRefreshToken(
                    response!!, jwtService.createAccessToken(user.name, user.email),
                    reIssuedRefreshToken
                )
            }
    }

    private fun reissueRefreshToken(user: User): String {
        val reIssuedRefreshToken = jwtService!!.createRefreshToken()
        user.refreshToken = reIssuedRefreshToken
        userRepository!!.saveAndFlush<User>(user)
        return reIssuedRefreshToken
    }

    @Throws(ServletException::class, IOException::class)
    fun checkAccessTokenAndAuthentication(
        request: HttpServletRequest?, response: HttpServletResponse?,
        filterChain: FilterChain
    ) {
        jwtService?.extractAccessToken(request!!)?.let { token ->
            if (jwtService.isTokenValid(token)) {
                jwtService.extractEmail(token)?.let { email ->
                    log.info { "email: $email" }
                    userRepository?.findByEmail(email)?.let { optionalUser ->
                        optionalUser.orElse(null)?.let { user ->
                            saveAuthentication(user)
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun saveAuthentication(user: User) {
        log.info { "saveAuthentication() user: $user" }
        var password: String = user.password
        if (password == "") {
            password = PasswordUtil().generateRandomPassword()
        }
        val userDetailsUser = org.springframework.security.core.userdetails.User.builder()
            .username(user.email)
            .password(password)
            .roles(user.role.name)
            .build()
        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            userDetailsUser, null,
            authoritiesMapper.mapAuthorities(userDetailsUser.authorities)
        )
        SecurityContextHolder.getContext().authentication = authentication
    }

}