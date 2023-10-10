package com.booster.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Entity
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @NotBlank(message = "Username is required")
    var email: String,
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    var name: String,
    var password: String
) {
    constructor() : this(null, "", "", "")
}
