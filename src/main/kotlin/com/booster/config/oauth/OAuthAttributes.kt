package com.booster.config.oauth

import com.booster.config.oauth.user.GoogleUser
import com.booster.config.oauth.user.KakaoUser
import com.booster.config.oauth.user.NaverUser
import com.booster.config.oauth.user.OAuth2UserInfo
import com.booster.entity.User
import com.booster.enums.Role
import com.booster.enums.SocialType
import java.util.*


class OAuthAttributes(
    nameAttributeKey: String, oauth2UserInfo: OAuth2UserInfo
) {
    val oauth2UserInfo: OAuth2UserInfo
    val nameAttributeKey: String

    init {
        this.oauth2UserInfo = oauth2UserInfo
        this.nameAttributeKey = nameAttributeKey
    }

    fun toEntity(socialType: SocialType?, oauth2UserInfo: OAuth2UserInfo): User {
        return User.Builder()
            .socialType(socialType)
            .socialId(oauth2UserInfo.id)
            .email(UUID.randomUUID().toString()+"@socialUser.com")
            .name(oauth2UserInfo.nickname!!)
            .imageUrl(oauth2UserInfo.imageUrl!!)
            .role(Role.ROLE_GUEST)
            .build()
    }

    companion object {
        fun of(
            socialType: SocialType,
            userNameAttributeName: String?, attributes: Map<String?, Any?>?
        ): OAuthAttributes {
            if (socialType === SocialType.NAVER) {
                return ofNaver(userNameAttributeName, attributes)
            }
            return if (socialType === SocialType.KAKAO) {
                ofKakao(userNameAttributeName, attributes)
            } else ofGoogle(userNameAttributeName, attributes)
        }

        private fun ofKakao(userNameAttributeName: String?, attributes: Map<String?, Any?>?): OAuthAttributes {
            return OAuthAttributes(userNameAttributeName!!, KakaoUser(attributes))
        }

        fun ofGoogle(userNameAttributeName: String?, attributes: Map<String?, Any?>?): OAuthAttributes {
            return OAuthAttributes(userNameAttributeName!!, GoogleUser(attributes))
        }

        fun ofNaver(userNameAttributeName: String?, attributes: Map<String?, Any?>?): OAuthAttributes {
            return OAuthAttributes(userNameAttributeName!!, NaverUser(attributes))
        }
    }
}