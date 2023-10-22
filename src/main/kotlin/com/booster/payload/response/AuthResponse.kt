package com.booster.payload.response

data class AuthResponse(
    var accessToken: String
) {
    constructor() : this("")
}