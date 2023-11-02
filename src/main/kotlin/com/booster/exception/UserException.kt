package com.booster.exception

import com.booster.enums.ErrorCode

class UserException(private val errorCode: ErrorCode) : Exception(errorCode.description) {
    companion object {
        fun alreadyExist(): UserException {
            return UserException(ErrorCode.USER_ALREADY_EXISTS)
        }

        fun notFound(): UserException {
            return UserException(ErrorCode.USER_NOT_FOUND)
        }
        fun unAuthorized(): UserException {
            return UserException(ErrorCode.UNAUTHORIZED)
        }

        fun forbidden(): UserException {
            return UserException(ErrorCode.FORBIDDEN)
        }
    }

    fun getHttpStatus(): Int{
        return errorCode.code
    }

    fun getErrorCode(): ErrorCode {
        return errorCode
    }

}
