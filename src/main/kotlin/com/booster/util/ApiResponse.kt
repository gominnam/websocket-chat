package com.booster.util

data class ApiResponse<T> (
    val status: Int? = null,
    val message: String? = null,
    val result: T? = null
) {
    class Builder<T> {
        private var status: Int? = null
        private var message: String? = null
        private var data: T? = null

        fun status(status: Int) = apply { this.status = status }
        fun message(message: String?) = apply { this.message = message}
        fun data(data: T?) = apply { this.data = data }
        fun build() = ApiResponse(status ?: HttpStatus.INTERNAL_SERVER_ERROR, message, data)
    }
}