package com.booster.dto

class UserDTO private constructor(
     var id: Long?,
     var email: String?,
     var name: String?,
     var password: String?
) {
    constructor(): this(null, null, null, null)
}