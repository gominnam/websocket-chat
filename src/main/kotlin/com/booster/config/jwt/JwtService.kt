package com.booster.config.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.booster.entity.User
import com.booster.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
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

    private val userRepository: UserRepository? = null

    val log = KotlinLogging.logger {}

    companion object {
        private const val ACCESS_TOKEN_SUBJECT = "AccessToken"
        private const val REFRESH_TOKEN_SUBJECT = "RefreshToken"
        private const val CLAIM = "email"
        private const val BEARER = "Bearer "
    }

    fun createAccessToken(email: String?): String {
        val now = Date()
        return JWT.create()
            .withSubject(ACCESS_TOKEN_SUBJECT)
            .withExpiresAt(Date(now.time + accessTokenExpirationTime!!))
            //추가적으로 식별자나, 이름 등의 정보를 더 추가하셔도 됩니다.
            //추가하실 경우 .withClaim(클래임 이름, 클래임 값) 으로 설정해주시면 됩니다
            .withClaim(CLAIM, email)
            .sign(Algorithm.HMAC512(secretKey))
    }

    fun createRefreshToken(): String {
        val now = Date()
        return JWT.create()
            .withSubject(REFRESH_TOKEN_SUBJECT)
            .withExpiresAt(Date(now.time + refreshTokenExpirationTime!!))
            .sign(Algorithm.HMAC512(secretKey))
    }

    fun sendAccessToken(response: HttpServletResponse, accessToken: String?) {
        response.status = HttpServletResponse.SC_OK
        response.setHeader(accessHeader, accessToken)
        log.info { "${"Access Token : {}"} $accessToken" }
    }

    fun sendAccessAndRefreshToken(response: HttpServletResponse, accessToken: String?, refreshToken: String?) {
        response.status = HttpServletResponse.SC_OK
        setAccessTokenHeader(response, accessToken)
        setRefreshTokenHeader(response, refreshToken)
        log.info { "set in header that Access Token, Refresh Token " }
    }

    fun extractRefreshToken(request: HttpServletRequest): Optional<String> {
        return Optional.ofNullable(request.getHeader(refreshHeader))
            .filter { refreshToken -> refreshToken.startsWith(BEARER) }
            .map { refreshToken -> refreshToken.replace(BEARER, "") }
    }

    fun extractAccessToken(request: HttpServletRequest): Optional<String> {
        return Optional.ofNullable(request.getHeader(accessHeader))
            .filter { refreshToken -> refreshToken.startsWith(BEARER) }
            .map { refreshToken -> refreshToken.replace(BEARER, "") }
    }

    fun extractEmail(accessToken: String?): Optional<String> {
        return try {
            Optional.ofNullable(
                JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(CLAIM)
                    .asString()
            )
        } catch (e: Exception) {
            log.error { "unvalided token" }
            Optional.empty()
        }
    }

    fun setAccessTokenHeader(response: HttpServletResponse, accessToken: String?) {
        response.setHeader(accessHeader, accessToken)
    }

    fun setRefreshTokenHeader(response: HttpServletResponse, refreshToken: String?) {
        response.setHeader(refreshHeader, refreshToken)
    }

    fun updateRefreshToken(email: String?, refreshToken: String) {
        userRepository!!.findByEmail(email!!)
            .ifPresentOrElse(
                { user: User ->
                    user.refreshToken = refreshToken
                }
            ) { throw Exception("not exists user") }
    }

    fun isTokenValid(token: String?): Boolean {
        return try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token)
            true
        } catch (e: Exception) {
            log.error { "${"not valid token {}"} ${e.message}" }
            false
        }
    }
}