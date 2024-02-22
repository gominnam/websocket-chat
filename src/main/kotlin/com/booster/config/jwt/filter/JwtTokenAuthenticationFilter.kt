package com.booster.config.jwt.filter

import com.booster.config.jwt.JwtService
import com.booster.config.jwt.PasswordUtil
import com.booster.config.login.CustomUserDetailsService
import com.booster.entity.User
import com.booster.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


class JwtTokenAuthenticationFilter(
    private val jwtService: JwtService,
    private val customUserDetailsService: CustomUserDetailsService,
    private val userRepository: UserRepository
)
    : OncePerRequestFilter() {

    private val authoritiesMapper: GrantedAuthoritiesMapper = NullAuthoritiesMapper()

    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }
        val jwtToken = authHeader!!.extractTokenValue()
        val email = jwtService.extractEmail(jwtToken)
        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            val foundUser = customUserDetailsService.loadUserByUsername(email)
            if (jwtService.isValid(jwtToken, foundUser))
                updateContext(foundUser, request)
            filterChain.doFilter(request, response)
        }
    }

    private fun String?.doesNotContainBearerToken() =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() =
        this.substringAfter("Bearer ")

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }

    private fun checkRefreshTokenAndReIssueAccessToken(response: HttpServletResponse?, refreshToken: String?) {
        userRepository.findByRefreshToken(refreshToken)
            .ifPresent { user ->
                val reIssuedRefreshToken = reissueRefreshToken(user)
                jwtService.sendAccessAndRefreshToken(
                    response!!, jwtService.createAccessToken(user.email),
                    reIssuedRefreshToken
                )
            }
    }

    private fun reissueRefreshToken(user: User): String {
        val reIssuedRefreshToken = jwtService.createRefreshToken()
        user.refreshToken = reIssuedRefreshToken
        userRepository.saveAndFlush<User>(user)
        return reIssuedRefreshToken
    }

    @Throws(ServletException::class, IOException::class)
    fun checkAccessTokenAndAuthentication(
        request: HttpServletRequest, response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        jwtService.extractAccessToken(request)?.let { token ->
            if (jwtService.isTokenValid(token)) {
                jwtService.extractEmail(token)?.let { email ->
                    userRepository.findByEmail(email).orElse(null)?.let { user ->
                        saveAuthentication(user)
                    }
                }
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun saveAuthentication(user: User) {
        log.info { "saveAuthentication() user: $user" }
        val password = user.password.ifEmpty { PasswordUtil().generateRandomPassword() }

        val userDetails = org.springframework.security.core.userdetails.User.builder()
            .username(user.email)
            .password(password)
            .roles(user.role.name)
            .build()

        val authentication = UsernamePasswordAuthenticationToken(
            userDetails, null,
            authoritiesMapper.mapAuthorities(userDetails.authorities)
        )

        SecurityContextHolder.getContext().authentication = authentication
    }

}