package com.booster.config.oauth

import com.booster.entity.User
import com.booster.enums.SocialType
import com.booster.repositories.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*


@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    companion object {
        private const val GOOGLE = "google"
        private const val NAVER = "naver"
        private const val KAKAO = "kakao"
    }

    private val log = KotlinLogging.logger {}

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        log.info{"CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입"}

        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val registrationId = userRequest.clientRegistration.registrationId
        val socialType = getSocialType(registrationId)
        val userNameAttributeName = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        val attributes = oAuth2User.attributes

        val extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes)
        val createdUser = getUser(extractAttributes, socialType)

        return CustomOAuth2User(
            Collections.singleton(SimpleGrantedAuthority(createdUser.role.name)),
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
        if (KAKAO == registrationId) {
            return SocialType.KAKAO
        }
        return SocialType.GOOGLE
    }

    private fun getUser(attributes: OAuthAttributes, socialType: SocialType): User =
        userRepository.findBySocialTypeAndSocialId(socialType, attributes.oauth2UserInfo.id.toString()).orElseGet {
            saveUser(attributes, socialType)
        }

    private fun saveUser(attributes: OAuthAttributes, socialType: SocialType): User {
        val createdUser = attributes.toEntity(socialType, attributes.oauth2UserInfo)
        return userRepository.save(createdUser)
    }
}

