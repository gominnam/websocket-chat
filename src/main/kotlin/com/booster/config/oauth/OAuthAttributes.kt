package com.booster.config.oauth

import com.booster.config.oauth.user.OAuth2UserInfo
import com.booster.entity.User
import com.booster.enums.Role
import com.booster.enums.SocialType
import java.util.*


class OAuthAttributes constructor(// OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private val nameAttributeKey: String, oauth2UserInfo: OAuth2UserInfo
) {
    private val oauth2UserInfo // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)
            : OAuth2UserInfo

    init {
        this.oauth2UserInfo = oauth2UserInfo
    }

    fun toEntity(socialType: SocialType?, oauth2UserInfo: OAuth2UserInfo): User {
        return User.Builder()
            .socialType(socialType)
            .socialId(oauth2UserInfo.id)
            .email(UUID.randomUUID().toString()+"@socialUser.com")
            .name(oauth2UserInfo.nickname!!)
            .imageUrl(oauth2UserInfo.imageUrl!!)
            .role(Role.ROLE_USER)
            .build()
    }

    companion object {
        /**
         * SocialType에 맞는 메소드 호출하여 OAuthAttributes 객체 반환
         * 파라미터 : userNameAttributeName -> OAuth2 로그인 시 키(PK)가 되는 값 / attributes : OAuth 서비스의 유저 정보들
         * 소셜별 of 메소드(ofGoogle, ofKaKao, ofNaver)들은 각각 소셜 로그인 API에서 제공하는
         * 회원의 식별값(id), attributes, nameAttributeKey를 저장 후 build
         */
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
            return builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(KakaoOAuth2UserInfo(attributes))
                .build()
        }

        fun ofGoogle(userNameAttributeName: String?, attributes: Map<String?, Any?>?): OAuthAttributes {
            return builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(GoogleOAuth2UserInfo(attributes))
                .build()
        }

        fun ofNaver(userNameAttributeName: String?, attributes: Map<String?, Any?>?): OAuthAttributes {
            return OAuthAttributes(userNameAttributeName, NaverOAuth2UserInfo(attributes))
        }
    }
}