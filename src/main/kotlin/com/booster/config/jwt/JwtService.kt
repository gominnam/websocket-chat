package com.booster.config.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.booster.entity.User
import com.booster.enums.ErrorCode
import com.booster.exception.AuthException
import com.booster.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
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

    fun createAccessToken(email: String?, name: String?): String {
        val now = Date()
        return JWT.create()
            .withSubject(ACCESS_TOKEN_SUBJECT)
            .withExpiresAt(Date(now.time + accessTokenExpirationTime!!))
            .withClaim(EMAIL_CLAIM, email)
            .withClaim(NAME_CLAIM, name)
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
        } catch (e: JWTVerificationException) { // JWT 라이브러리가 발생시킬 수 있는 구체적인 예외 유형
            throw AuthException(ErrorCode.TOKEN_INVALID)
        } catch (e: Exception) {
            log.error { "Unexpected error: ${e.message}" }
            return null
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
                user.refreshToken = refreshToken
                userRepository.saveAndFlush(user)
            }
        ) { throw Exception("not exists user") }
    }

    fun isTokenValid(token: String?): Boolean {
        return try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isValid(token: String, userDetails: UserDetails): Boolean {
        val email = extractEmail(token)
        return userDetails.username == email && isTokenValid(token)
    }

    fun issueTokens(email: String, name: String): Pair<String, String> {
        val accessToken = createAccessToken(email, name)
        val refreshToken = createRefreshToken()
        updateRefreshToken(email, refreshToken)
        return Pair(accessToken, refreshToken)
    }
}
//
//data class AuthContext(val user: User, val isAdmin: Boolean)
//fun Authentication.toUser(): User {
//    return (this.principal as AuthContext).user
//}
