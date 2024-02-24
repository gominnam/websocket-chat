package com.booster.config.oauth.userinfo

class GoogleUserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val id: String?
        get() = attributes["sub"] as String?

    override val nickname: String?
        get() = attributes["name"] as String?

    override val imageUrl: String?
        get() = attributes["picture"] as String?
}