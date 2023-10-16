package com.booster.payload.response

data class AuthResponse(
    var token: String
) {
    constructor() : this("")
}