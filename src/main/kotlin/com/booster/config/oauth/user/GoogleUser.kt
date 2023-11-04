package com.booster.config.oauth.user


class GoogleUser(attributes: Map<String?, Any?>?) : OAuth2UserInfo(attributes) {
    override val id: String?
        get() = attributes?.get("sub") as String?
    override val nickname: String?
        get() = attributes?.get("name") as String?
    override val imageUrl: String?
        get() = attributes?.get("picture") as String?
}