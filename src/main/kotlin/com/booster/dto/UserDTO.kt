package com.booster.dto

class UserDTO private constructor(
     var id: Long?,
     var email: String?,
     var name: String?,
     var password: String?
) {
   data class Builder(
        var id: Long? = null,
        var email: String? = null,
        var name: String? = null,
        var password: String? = null
    ) {
        fun id(id: Long) = apply { this.id = id }
        fun email(email: String) = apply { this.email = email }
        fun name(name: String) = apply { this.name = name }
        fun password(password: String) = apply { this.password = password }
        fun build() = UserDTO(id, email, name, password)
    }
}