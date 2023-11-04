package com.booster.config.jwt

import kotlin.random.Random

class PasswordUtil {
    companion object {
        const val PASSWORD_LENGTH = 8
    }

    fun generateRandomPassword(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') // 알파벳과 숫자

        return (1..PASSWORD_LENGTH)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}