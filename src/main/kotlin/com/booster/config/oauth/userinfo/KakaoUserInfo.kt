package com.booster.config.oauth.userinfo

class KakaoUserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val id: String
        get() = attributes["id"].toString()

    override val email: String
        get() {
            val account = attributes["kakao_account"] as Map<*, *>? ?: return ""

            return account["email"] as String
        }

    override val nickname: String?
        get() {
            val account = attributes["kakao_account"] as Map<*, *>?
            val profile = account!!["profile"] as Map<*, *>? ?: return null

            return profile["nickname"] as String
        }

    override val imageUrl: String?
        get() {
            val account = attributes["kakao_account"] as Map<*, *>?
            val profile = account!!["profile"] as Map<*, *>? ?: return null

            return profile["thumbnail_image_url"] as String?
        }
}