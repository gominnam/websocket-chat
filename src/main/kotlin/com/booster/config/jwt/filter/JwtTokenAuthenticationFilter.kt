package com.booster.config.jwt.filter

import com.booster.config.SecurityConfigProperties.Companion.NO_CHECK_URLS
import com.booster.config.jwt.JwtService
import com.booster.config.login.CustomUserDetailsService
import com.booster.repositories.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter


class JwtTokenAuthenticationFilter(
    private val jwtService: JwtService,
    private val customUserDetailsService: CustomUserDetailsService,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")
        val refreshHeader: String? = request.getHeader("Authorization-refresh")

        if (authHeader != null && authHeader.doesNotContainBearerToken()) {
            validateAndAuthenticate(authHeader, request)
        } else if (refreshHeader != null && refreshHeader.doesNotContainBearerToken()) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshHeader.extractTokenValue())
        } else if (!request.isUrlExcluded()) {
            handleUnauthenticatedRequests(request, response)
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun String?.doesNotContainBearerToken() =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() =
        this.substringAfter("Bearer ")

    private fun validateAndAuthenticate(header: String, request: HttpServletRequest) {
        val token = header.extractTokenValue()
        val subject = jwtService.extractEmail(token)
        if (subject != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = customUserDetailsService.loadUserByUsername(subject)
            if (jwtService.isValid(token, userDetails)) {
                updateContext(userDetails, request)
            }
        }
    }

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }

    private fun checkRefreshTokenAndReIssueAccessToken(response: HttpServletResponse?, refreshToken: String?) {
        userRepository.findByRefreshToken(refreshToken)
            .ifPresent { user ->
                val reIssuedAccessToken = jwtService.createAccessToken(user.email)
                jwtService.sendAccessToken(response!!, reIssuedAccessToken)
            }
    }

    private fun handleUnauthenticatedRequests(request: HttpServletRequest, response: HttpServletResponse) {
        val acceptHeader = request.getHeader("Accept")
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write("{\"error\": \"Unauthenticated\"}")
        response.sendRedirect("/")
    }

    private fun HttpServletRequest.isUrlExcluded(): Boolean {
        val requestURL = this.requestURI.toString()
        val pathMatcher = AntPathMatcher()

        return NO_CHECK_URLS.any { pattern ->
            pathMatcher.match(pattern, requestURL)
        }
    }
}