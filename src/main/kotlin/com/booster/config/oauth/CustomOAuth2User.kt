package com.booster.config.oauth

import com.booster.enums.Role
import org.springframework.security.core.GrantedAuthority

import org.springframework.security.oauth2.core.user.DefaultOAuth2User


class CustomOAuth2User(
    authorities: Collection<GrantedAuthority?>?,
    attributes: Map<String?, Any?>?, nameAttributeKey: String?,
    private val email: String, role: Role
) : DefaultOAuth2User(authorities, attributes, nameAttributeKey) {
    private val role: Role

    init {
        this.role = role
    }
}