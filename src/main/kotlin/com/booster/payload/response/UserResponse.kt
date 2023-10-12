package com.booster.payload.response

data class UserResponse (
    var id: Long?,
    var email: String?,
    var name: String?,
    var password: String?
){
   constructor() : this(null, null, null, null)
}