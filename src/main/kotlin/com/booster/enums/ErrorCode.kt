package com.booster.enums

import com.booster.util.HttpStatus

enum class ErrorCode(val code: Int, val description: String) {
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "User already exists"),
    USER_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "Password not match"),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "Unauthorised or Bad Credentials"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),
    HEADER_ERROR(HttpStatus.BAD_REQUEST, "Header Error"),
}