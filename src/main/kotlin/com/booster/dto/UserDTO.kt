package com.booster.dto

import com.booster.entity.User

data class UserDTO (
     var id: Long?,
     var email: String?,
     var name: String?,
     var password: String?
) {
    constructor(): this(null, null, null, null)
}