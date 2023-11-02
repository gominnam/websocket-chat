package com.booster.exception

import com.booster.enums.ErrorCode

class CommonException(private val errorCode: ErrorCode) : Exception(errorCode.description){
    companion object {
        fun unAuthorized(): CommonException {
            return CommonException(ErrorCode.UNAUTHORIZED)
        }

        fun forbidden(): CommonException {
            return CommonException(ErrorCode.FORBIDDEN)
        }
    }

    fun getHttpStatus(): Int{
        return errorCode.code
    }

    fun getErrorCode(): ErrorCode {
        return errorCode
    }

}