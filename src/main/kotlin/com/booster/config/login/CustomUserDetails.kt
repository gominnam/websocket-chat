package com.booster.config.login

import com.booster.enums.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val email: String, // 이메일을 username으로 사용
    private val password: String,
    private val name: String,
    private val authorities: Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_USER"), SimpleGrantedAuthority("ROLE_ADMIN"), SimpleGrantedAuthority("ROLE_GUEST"))
) : UserDetails {
    override fun getUsername(): String = email
    override fun getPassword(): String = password
    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
    fun getName(): String = name

    class Builder {
        private lateinit var email: String
        private lateinit var password: String
        private lateinit var name: String
        private var authorities: Collection<GrantedAuthority> = listOf(
            SimpleGrantedAuthority("ROLE_USER"),
            SimpleGrantedAuthority("ROLE_ADMIN"),
            SimpleGrantedAuthority("ROLE_GUEST")
        )

        fun email(email: String) = apply { this.email = email }
        fun password(password: String) = apply { this.password = password }
        fun name(name: String) = apply { this.name = name }
        fun authorities(authorities: Role) = apply { this.authorities = listOf(SimpleGrantedAuthority("ROLE_"+authorities.name))}
        fun build() = CustomUserDetails(email, password, name, authorities)
    }
}