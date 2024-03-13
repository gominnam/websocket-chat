package com.booster.config.oauth

import com.booster.config.util.CookieUtils.addCookie
import com.booster.config.util.CookieUtils.deleteCookie
import com.booster.config.util.CookieUtils.deserialize
import com.booster.config.util.CookieUtils.getCookie
import com.booster.config.util.CookieUtils.serialize
import com.nimbusds.oauth2.sdk.util.StringUtils
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component


@Component
class HttpCookieOAuth2AuthorizationRequestRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest?> {
    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        return getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_NAME)
            .map { cookie: Cookie? ->
                deserialize(
                    cookie!!,
                    OAuth2AuthorizationRequest::class.java
                )
            }
            .orElse(null)
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest, response: HttpServletResponse
    ) {
        if (authorizationRequest == null) {
            deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_NAME)
            deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
            return
        }

        addCookie(
            response, OAUTH2_AUTHORIZATION_REQUEST_NAME,
            serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS
        )

        val redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME)
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, COOKIE_EXPIRE_SECONDS)
        }
    }

    override fun removeAuthorizationRequest(request: HttpServletRequest, response: HttpServletResponse?): OAuth2AuthorizationRequest? {
        val authorizationRequest = this.loadAuthorizationRequest(request)
        if (response != null) {
            this.removeAuthorizationRequestCookies(request, response)
        }
        return authorizationRequest
    }

    fun removeAuthorizationRequestCookies(
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ) {
        deleteCookie(request!!, response!!, OAUTH2_AUTHORIZATION_REQUEST_NAME)
        deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
    }

    companion object {
        const val OAUTH2_AUTHORIZATION_REQUEST_NAME: String = "oauth2_auth_request"
        const val REDIRECT_URI_PARAM_COOKIE_NAME: String = "redirect_uri"
        private const val COOKIE_EXPIRE_SECONDS = 180
    }
}