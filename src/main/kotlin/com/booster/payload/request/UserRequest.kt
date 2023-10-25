package com.booster.payload.request

data class UserRequest(
    var id: Long?,
    var email: String?,
    var name: String?,
    var password: String?
) {
    constructor() : this(null, null, null, null)

    class Builder {
        private var id: Long? = null
        private var email: String? = null
        private var name: String? = null
        private var password: String? = null

        fun id(id: Long?) = apply { this.id = id }
        fun email(email: String?) = apply { this.email = email }
        fun name(name: String?) = apply { this.name = name }
        fun password(password: String?) = apply { this.password = password }

        fun build() = UserRequest(id, email, name, password)
    }
}

