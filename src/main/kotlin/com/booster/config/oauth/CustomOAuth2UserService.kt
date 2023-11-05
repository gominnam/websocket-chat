package com.booster.config.oauth

import com.booster.entity.User
import com.booster.enums.SocialType
import com.booster.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*


@Service
class CustomOAuth2UserService : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private val userRepository: UserRepository? = null

    val log = KotlinLogging.logger {}

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate: OAuth2UserService<OAuth2UserRequest, OAuth2User> = DefaultOAuth2UserService()
        val oAuth2User: OAuth2User = delegate.loadUser(userRequest)

        /**
         * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
         * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
         * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
         */
        val registrationId = userRequest.clientRegistration.registrationId
        val socialType = getSocialType(registrationId)
        val userNameAttributeName = userRequest.clientRegistration
            .providerDetails.userInfoEndpoint.userNameAttributeName
        val attributes = oAuth2User.attributes

        val extractAttributes: OAuthAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes)
        val createdUser: User = getUser(extractAttributes, socialType) // getUser() 메소드로 User 객체 생성 후 반환

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        return CustomOAuth2User(
            Collections.singleton(SimpleGrantedAuthority(createdUser.role.toString())),
            attributes,
            extractAttributes.nameAttributeKey,
            createdUser.email,
            createdUser.role
        )
    }

    private fun getSocialType(registrationId: String): SocialType {
        if (NAVER == registrationId) {
            return SocialType.NAVER
        }
        return if (KAKAO == registrationId) {
            SocialType.KAKAO
        } else SocialType.GOOGLE
    }

    private fun getUser(attributes: OAuthAttributes, socialType: SocialType): User {
        return userRepository?.findBySocialTypeAndSocialId(
            socialType, attributes.oauth2UserInfo.id!!
        )?.orElse(null) ?: return saveUser(attributes, socialType)
    }

    private fun saveUser(attributes: OAuthAttributes, socialType: SocialType): User {
        val createdUser: User = attributes.toEntity(socialType, attributes.oauth2UserInfo)
        return userRepository!!.save(createdUser)
    }

    companion object {
        private const val NAVER = "naver"
        private const val KAKAO = "kakao"
    }
}