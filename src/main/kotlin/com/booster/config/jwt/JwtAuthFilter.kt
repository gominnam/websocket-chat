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


class JwtAuthFilter : OncePerRequestFilter() {
    private val jwtService: JwtService? = null
    private val userRepository: UserRepository? = null
    private val authoritiesMapper: GrantedAuthoritiesMapper = NullAuthoritiesMapper()

    companion object {
        private const val NO_CHECK_URL = "/api/user/login"
    }

    val log = KotlinLogging.logger {}

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI.equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response)
            return
        }

        val refreshToken = jwtService!!.extractRefreshToken(request)
            .filter { token: String? -> jwtService.isTokenValid(token) }
            .orElse(null)

        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken)
            return
        }

        checkAccessTokenAndAuthentication(request, response, filterChain)
    }

    /**
     * [리프레시 토큰으로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드]
     * 파라미터로 들어온 헤더에서 추출한 리프레시 토큰으로 DB에서 유저를 찾고, 해당 유저가 있다면
     * JwtService.createAccessToken()으로 AccessToken 생성,
     * reIssueRefreshToken()로 리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드 호출
     * 그 후 JwtService.sendAccessTokenAndRefreshToken()으로 응답 헤더에 보내기
     */
    private fun checkRefreshTokenAndReIssueAccessToken(response: HttpServletResponse?, refreshToken: String?) {
        userRepository?.findByRefreshToken(refreshToken)
            ?.ifPresent { user ->
                val reIssuedRefreshToken = reissueRefreshToken(user)
                jwtService!!.sendAccessAndRefreshToken(
                    response!!, jwtService.createAccessToken(user.email),
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

    /**
     * [액세스 토큰 체크 & 인증 처리 메소드]
     * 유효한 토큰이면, 액세스 토큰에서 extractEmail로 Email을 추출한 후 findByEmail()로 해당 이메일을 사용하는 유저 객체 반환
     * 그 유저 객체를 saveAuthentication()으로 인증 처리하여
     * 인증 허가 처리된 객체를 SecurityContextHolder에 담기
     * 그 후 다음 인증 필터로 진행
     */
    @Throws(ServletException::class, IOException::class)
    fun checkAccessTokenAndAuthentication(
        request: HttpServletRequest?, response: HttpServletResponse?,
        filterChain: FilterChain
    ) {
        jwtService!!.extractAccessToken(request!!)
            .filter { token: String? -> jwtService.isTokenValid(token) }
            .ifPresent { accessToken: String? ->
                jwtService.extractEmail(accessToken)
                    .ifPresent { email: String? ->
                        userRepository!!.findByEmail(email!!)
                            .ifPresent { user: User ->
                                saveAuthentication(user)
                            }
                    }
            }
        filterChain.doFilter(request, response)
    }

    fun saveAuthentication(user: User) {
        var password: String = user.password
        if (password == "" || password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
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