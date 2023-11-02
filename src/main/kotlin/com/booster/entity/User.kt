package com.booster.entity

import com.booster.entity.util.AuditLog
import com.booster.enums.Role
import com.booster.enums.SocialType
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

@Entity
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    var email: String,
    @NotBlank(message = "Username is required")
    @Length(min = 3, max = 15)
    var name: String,
    @Length(min = 7, max = 15)
    var password: String,
    @Enumerated(EnumType.STRING)
    var role: Role,
    @Enumerated(EnumType.STRING)
    var socialType: SocialType?, // KAKAO, NAVER, GOOGLE
    var socialId: String?, // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)
    var refreshToken: String,
) : AuditLog() {
    constructor() : this(null, "", "", "", Role.ROLE_USER, null, null, "")

    class Builder{
        private var id: Long? = null
        private var email: String = ""
        private var name: String = ""
        private var password: String = ""
        private var role: Role = Role.ROLE_USER
        private var socialType: SocialType? = null
        private var socialId: String? = null
        private var refreshToken: String = ""

        fun id(id: Long?) = apply { this.id = id }
        fun email(email: String) = apply { this.email = email }
        fun name(name: String) = apply { this.name = name }
        fun password(password: String) = apply { this.password = password }
        fun role(role: Role) = apply { this.role = role }
        fun socialType(socialType: SocialType?) = apply { this.socialType = socialType }
        fun socialId(socialId: String?) = apply { this.socialId = socialId }
        fun refreshToken(refreshToken: String) = apply { this.refreshToken = refreshToken }
        fun build() = User(id, email, name, password, role, socialType, socialId, refreshToken)
    }
}
