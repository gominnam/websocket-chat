package com.booster.dto

data class UserDTO (
     var id: Long? = null,
     var email: String,
     var name: String,
     var password: String
) {
    constructor() : this(null, "", "", "")
}