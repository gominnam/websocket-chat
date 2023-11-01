package com.booster.entity

import com.booster.entity.util.AuditLog
import com.booster.model.Role
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
    var role: Role
) : AuditLog() {
    constructor() : this(null, "", "", "", Role.ROLE_USER)
}
