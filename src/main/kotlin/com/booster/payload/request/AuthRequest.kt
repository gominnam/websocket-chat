package com.booster.payload.request

class AuthRequest(
    var name: String?,
) {
    constructor() : this(null)
    class Builder {
        private var name: String? = null

        fun name(name: String?) = apply { this.name = name }

        fun build() = AuthRequest(name)
    }
}