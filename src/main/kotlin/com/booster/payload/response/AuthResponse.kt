package com.booster.payload.response

data class AuthResponse(
    var accessToken: String?,
    var refreshToken: String?
) {
    constructor() : this("", "")
}