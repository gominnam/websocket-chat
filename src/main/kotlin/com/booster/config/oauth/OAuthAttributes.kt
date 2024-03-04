package com.booster.config.oauth

import com.booster.config.oauth.userinfo.GoogleUserInfo
import com.booster.config.oauth.userinfo.OAuth2UserInfo
import com.booster.entity.User
import com.booster.enums.Role
import com.booster.enums.SocialType
import java.util.*

data class OAuthAttributes(
    val nameAttributeKey: String,
    val oauth2UserInfo: OAuth2UserInfo
) {
    companion object {
        fun of(socialType: SocialType, userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return when (socialType) {
                SocialType.GOOGLE -> ofGoogle(userNameAttributeName, attributes)
                else -> throw IllegalArgumentException("Unsupported social type")
            }
        }

        private fun ofGoogle(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                nameAttributeKey = userNameAttributeName,
                oauth2UserInfo = GoogleUserInfo(attributes)
            )
        }

    }

    fun toEntity(socialType: SocialType, oauth2UserInfo: OAuth2UserInfo): User {
        return User.Builder()
            .email(oauth2UserInfo.email!!)
//            .email(UUID.randomUUID().toString() + "@socialUser.com")
            .name(oauth2UserInfo.nickname!!)
            .imageUrl(oauth2UserInfo.imageUrl!!)
            .role(Role.GUEST)
            .socialType(socialType)
            .socialId(oauth2UserInfo.id)
            .build()
    }
}
