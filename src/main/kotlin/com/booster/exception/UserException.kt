package com.booster.exception

class UserException(private val errorCode: ErrorCode) : Exception(errorCode.description) {
    fun getHttpStatus(): Int{
        return errorCode.code
    }

    fun getErrorCode(): ErrorCode{
        return errorCode
    }
}
