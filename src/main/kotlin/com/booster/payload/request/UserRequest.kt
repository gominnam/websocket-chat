package com.booster.payload.request

data class UserRequest(
    var id: Long?,
    var email: String?,
    var name: String?,
    var password: String?
) {
    constructor() : this(null, null, null, null)
}

