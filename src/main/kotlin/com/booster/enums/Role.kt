package com.booster.enums

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role{
    GUEST,
    USER,
    ADMIN;

    fun getGrantedAuthority(): GrantedAuthority {
        return SimpleGrantedAuthority("ROLE_$name")
    }
}