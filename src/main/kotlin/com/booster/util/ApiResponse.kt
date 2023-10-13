package com.booster.util

data class ApiResponse<T> (
    val status: HttpStatus? = null,
    val message: String? = null,
    val result: T? = null
) {
    class Builder<T> {
        private var status: HttpStatus? = null
        private var message: String? = null
        private var result: T? = null

        fun status(status: HttpStatus) = apply { this.status = status }
        fun message(message: String?) = apply { this.message = message}
        fun result(result: T?) = apply { this.result = result }
        fun build() = ApiResponse(status, message, result)
    }
}