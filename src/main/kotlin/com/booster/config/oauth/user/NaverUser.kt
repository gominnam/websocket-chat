package com.booster.config.oauth.user


class NaverUser(attributes: Map<String?, Any?>?) : OAuth2UserInfo(attributes) {
    override val id: String?
        get() {
            val response = attributes!!["response"] as Map<*, *>? ?: return null
            return response["id"] as String?
        }
    override val nickname: String?
        get() {
            val response = attributes!!["response"] as Map<*, *>? ?: return null
            return response["nickname"] as String?
        }
    override val imageUrl: String?
        get() {
            val response =
                attributes!!["response"] as Map<*, *>? ?: return null
            return response["profile_image"] as String?
        }
}