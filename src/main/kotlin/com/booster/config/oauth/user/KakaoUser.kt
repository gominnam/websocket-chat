package com.booster.config.oauth.user


class KakaoUser(attributes: Map<String?, Any?>?) : OAuth2UserInfo(attributes) {
    override val id: String
        get() = attributes?.get("id").toString()
    override val nickname: String?
        get() {
            val account = attributes?.get("kakao_account") as Map<*, *>?
            val profile = account?.get("profile") as Map<*, *>?
            return if (attributes == null || profile == null) {
                null
            } else profile["nickname"] as String?
        }
    override val imageUrl: String?
        get() {
            val account = attributes?.get("kakao_account") as Map<*, *>?
            val profile = account?.get("profile") as Map<*, *>?
            return if (account == null || profile == null) {
                null
            } else profile["thumbnail_image_url"] as String?
        }
}