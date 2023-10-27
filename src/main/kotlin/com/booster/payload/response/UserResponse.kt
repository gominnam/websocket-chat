package com.booster.payload.response

data class UserResponse (
    var id: Long?,
    var email: String?,
    var name: String?,
){
    constructor() : this(null, null, null)

    class Builder {
        private var id: Long? = null
        private var email: String? = null
        private var name: String? = null

        fun id(id: Long?) = apply { this.id = id }
        fun email(email: String?) = apply { this.email = email }
        fun name(name: String?) = apply { this.name = name }

        fun build() = UserResponse(id, email, name)
    }}

