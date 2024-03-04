package com.booster.exception

import com.booster.enums.ErrorCode

class AuthException(private val errorCode: ErrorCode) : Exception(errorCode.description) {
    companion object {
        fun tokenInvalid(): AuthException {
            return AuthException(ErrorCode.TOKEN_INVALID)
        }

        fun tokenExpired(): AuthException {
            return AuthException(ErrorCode.TOKEN_EXPIRED)
        }
    }

    fun getHttpStatus(): Int {
        return errorCode.code
    }

    fun getErrorCode(): ErrorCode {
        return errorCode
    }
}