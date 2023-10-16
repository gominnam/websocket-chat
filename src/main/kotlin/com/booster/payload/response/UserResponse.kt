package com.booster.payload.response

data class UserResponse (
    var id: Long?,
    var email: String?,
    var name: String?,
){
    constructor() : this(null, null, null)
}