package com.booster.exception

import com.booster.util.HttpStatus

enum class ErrorCode(val code: Int, val description: String) {
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "User already exists"),
    USER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "User not authorized"),
}