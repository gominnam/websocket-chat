package com.booster.config.oauth

import com.booster.enums.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.DefaultOAuth2User

class CustomOAuth2User(
    authorities: Collection<GrantedAuthority>,
    attributes: Map<String, Any>,
    nameAttributeKey: String,
    val email: String,
    val role: Role
) : DefaultOAuth2User(authorities, attributes, nameAttributeKey)
