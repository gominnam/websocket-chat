package com.booster.config.oauth.handler

import com.booster.config.jwt.JwtService
import com.booster.config.oauth.CustomOAuth2User
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
data class OAuth2SuccessHandler(val jwtService: JwtService? = null) : AuthenticationSuccessHandler {

    val log = KotlinLogging.logger {}

    //    private final UserRepository userRepository;
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        log.info { "OAuth2 Login 성공!" }
        try {
            val oAuth2User: CustomOAuth2User = authentication.principal as CustomOAuth2User

            // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if (oAuth2User.getRole() === Role.GUEST) {
                val accessToken: String = jwtService.createAccessToken(oAuth2User.getEmail())
                response.addHeader(jwtService.getAccessHeader(), "Bearer $accessToken")
                response.sendRedirect("oauth2/sign-up") // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
                jwtService.sendAccessAndRefreshToken(response, accessToken, null)
                //                User findUser = userRepository.findByEmail(oAuth2User.getEmail())
//                                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
//                findUser.authorizeUser();
            } else {
                loginSuccess(response, oAuth2User) // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
        } catch (e: Exception) {
            throw e
        }
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    @Throws(IOException::class)
    private fun loginSuccess(response: HttpServletResponse, oAuth2User: CustomOAuth2User) {
        val accessToken: String = jwtService.createAccessToken(oAuth2User.getEmail())
        val refreshToken: String = jwtService.createRefreshToken()
        response.addHeader(jwtService.getAccessHeader(), "Bearer $accessToken")
        response.addHeader(jwtService.getRefreshHeader(), "Bearer $refreshToken")
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken)
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken)
    }
}
