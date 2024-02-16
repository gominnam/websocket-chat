package com.booster.config.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.booster.entity.User
import com.booster.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*


@Service
class JwtService {
    @Value("\${jwt.secret.key}")
    private val secretKey: String? = null

    @Value("\${jwt.token.expiration.time}")
    private val accessTokenExpirationTime: Long? = null

    @Value("\${jwt.token.refresh.expiration.time}")
    private val refreshTokenExpirationTime: Long? = null

    @Value("\${jwt.access.header}")
    val accessHeader: String? = null

    @Value("\${jwt.refresh.header}")
    val refreshHeader: String? = null

    @Autowired
    private val userRepository: UserRepository? = null

    val log = KotlinLogging.logger {}

    companion object {
        private const val ACCESS_TOKEN_SUBJECT = "AccessToken"
        private const val REFRESH_TOKEN_SUBJECT = "RefreshToken"
        private const val NAME_CLAIM = "name"
        private const val EMAIL_CLAIM = "email"
        private const val BEARER = "Bearer "
    }

    fun createAccessToken(name: String?, email: String?): String {
        val now = Date()
        return JWT.create()
            .withSubject(ACCESS_TOKEN_SUBJECT)
            .withExpiresAt(Date(now.time + accessTokenExpirationTime!!))
            //추가할 경우 .withClaim(클래임 이름, 클래임 값) 으로 설정
            .withClaim(NAME_CLAIM, name)
            .withClaim(EMAIL_CLAIM, email)
            .sign(Algorithm.HMAC512(secretKey))
    }

    fun createRefreshToken(): String {
        val now = Date()
        return JWT.create()
            .withSubject(REFRESH_TOKEN_SUBJECT)
            .withExpiresAt(Date(now.time + refreshTokenExpirationTime!!))
            .sign(Algorithm.HMAC512(secretKey))
    }

    /**
     * Access Token을 Response Header에 담아서 보내주는 메소드
     */
    fun sendAccessToken(response: HttpServletResponse, accessToken: String?) {
        response.status = HttpServletResponse.SC_OK
        response.setHeader(accessHeader, accessToken)
    }

    /**
     * Access Token과 Refresh Token을 Response Header에 담아서 보내주는 메소드
     */
    fun sendAccessAndRefreshToken(response: HttpServletResponse, accessToken: String?, refreshToken: String?) {
        response.status = HttpServletResponse.SC_OK
        setAccessTokenHeader(response, accessToken)
        setRefreshTokenHeader(response, refreshToken)
    }

    fun extractRefreshToken(request: HttpServletRequest): String? {
        return request.getHeader(refreshHeader)?.takeIf { it.startsWith(BEARER) }?.substring(BEARER.length)
    }

    fun extractAccessToken(request: HttpServletRequest): String? {
        return request.getHeader(accessHeader)?.takeIf { it.startsWith(BEARER) }?.substring(BEARER.length)
    }

    fun extractEmail(accessToken: String?): String? {
        return try {
            JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(accessToken)
                .getClaim(EMAIL_CLAIM).asString()
        } catch (e: Exception) {
            log.error { "unvalided token" }
            null
        }
    }

    fun setAccessTokenHeader(response: HttpServletResponse, accessToken: String?) {
        response.setHeader(accessHeader, accessToken)
    }

    fun setRefreshTokenHeader(response: HttpServletResponse, refreshToken: String?) {
        response.setHeader(refreshHeader, refreshToken)
    }

    fun updateRefreshToken(email: String?, refreshToken: String) {
        email ?: throw IllegalArgumentException("Email cannot be null")
        log.info { "email : $email, refreshToken : $refreshToken"}
        userRepository?.findByEmail(email)?.ifPresentOrElse(
            { user: User ->
                userRepository.updateRefreshTokenByEmail(user.email, refreshToken)
            }
        ) { throw Exception("not exists user") }
    }

    fun isTokenValid(token: String?): Boolean {
        return try {
            log.info { "token : $token" }
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token)
            true
        } catch (e: Exception) {
            log.error { "${"not valid token {}"} ${e.message}" }
            false
        }
    }

    fun issueTokens(name: String, email: String): Pair<String, String> {
        val accessToken = createAccessToken(name, email)
        val refreshToken = createRefreshToken()
        updateRefreshToken(email, refreshToken)
        return Pair(accessToken, refreshToken)
    }
}

data class AuthContext(val user: User, val isAdmin: Boolean)
fun Authentication.toUser(): User {
    return (this.principal as AuthContext).user
}
/*
* fun toUser(authentication: Authentication): User {
*   return (authentication.principal as AuthContext).user
* }
*  */
