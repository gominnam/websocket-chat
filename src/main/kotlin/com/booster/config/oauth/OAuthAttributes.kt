package com.booster.config.oauth

import com.booster.config.oauth.userinfo.GoogleUserInfo
import com.booster.config.oauth.userinfo.KakaoUserInfo
import com.booster.config.oauth.userinfo.NaverUserInfo
import com.booster.config.oauth.userinfo.OAuth2UserInfo
import com.booster.entity.User
import com.booster.enums.Role
import com.booster.enums.SocialType


data class OAuthAttributes(
    val nameAttributeKey: String,
    val oauth2UserInfo: OAuth2UserInfo
) {
    companion object {
        fun of(socialType: SocialType, userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return when (socialType) {
                SocialType.GOOGLE -> ofGoogle(userNameAttributeName, attributes)
                SocialType.NAVER -> ofNaver(userNameAttributeName, attributes)
                SocialType.KAKAO -> ofKakao(userNameAttributeName, attributes)
            }
        }

        private fun ofGoogle(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                nameAttributeKey = userNameAttributeName,
                oauth2UserInfo = GoogleUserInfo(attributes)
            )
        }

        private fun ofNaver(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                nameAttributeKey = userNameAttributeName,
                oauth2UserInfo = NaverUserInfo(attributes)
            )
        }

        private fun ofKakao(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                nameAttributeKey = userNameAttributeName,
                oauth2UserInfo = KakaoUserInfo(attributes)
            )
        }
    }

    fun toEntity(socialType: SocialType, oauth2UserInfo: OAuth2UserInfo): User {
        return User.Builder()
            .email(oauth2UserInfo.email!!)
            .name(oauth2UserInfo.nickname!!)
            .imageUrl(oauth2UserInfo.imageUrl!!)
            .role(Role.GUEST)
            .socialType(socialType)
            .socialId(oauth2UserInfo.id)
            .build()
    }
}
