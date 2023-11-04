package com.booster.config.oauth.user

abstract class OAuth2UserInfo(protected var attributes: Map<String?, Any?>?) {

    abstract val id: String?
    abstract val nickname: String?
    abstract val imageUrl: String?
}