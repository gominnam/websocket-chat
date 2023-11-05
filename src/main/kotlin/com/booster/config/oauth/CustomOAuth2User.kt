package com.booster.config.oauth

import com.booster.enums.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.DefaultOAuth2User


class CustomOAuth2User(
    authorities: Collection<GrantedAuthority?>?,
    attributes: Map<String?, Any?>?, nameAttributeKey: String?,
    email: String, role: Role
) : DefaultOAuth2User(authorities, attributes, nameAttributeKey) {
    val role: Role
    val email: String

    init {
        this.role = role
        this.email = email
    }
}