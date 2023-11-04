package com.booster.config.oauth

import com.booster.enums.SocialType
import com.booster.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User


class OAuth2UserService {

}


@Slf4j
@Service
@RequiredArgsConstructor
class CustomOAuth2UserService : OAuth2UserService<OAuth2UserRequest?, OAuth2User?> {
    private val userRepository: UserRepository? = null
    @Throws(OAuth2AuthenticationException::class)
    fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입")
        /**
         * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
         * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
         * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
         * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
         */
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
            .providerDetails.userInfoEndpoint.userNameAttributeName // OAuth2 로그인 시 키(PK)가 되는 값
        val attributes = oAuth2User.attributes // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        val extractAttributes: OAuthAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes)
        val createdUser: User = getUser(extractAttributes, socialType) // getUser() 메소드로 User 객체 생성 후 반환

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        return CustomOAuth2User(
            Collections.singleton(SimpleGrantedAuthority(createdUser.getRole().getKey())),
            attributes,
            extractAttributes.getNameAttributeKey(),
            createdUser.getEmail(),
            createdUser.getRole()
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

    /**
     * SocialType과 attributes에 들어있는 소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메소드
     * 만약 찾은 회원이 있다면, 그대로 반환하고 없다면 saveUser()를 호출하여 회원을 저장한다.
     */
    private fun getUser(attributes: OAuthAttributes, socialType: SocialType): User {
        return userRepository.findBySocialTypeAndSocialId(
            socialType,
            attributes.getOauth2UserInfo().getId()
        ).orElse(null) ?: return saveUser(attributes, socialType)
    }

    /**
     * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
     * 생성된 User 객체를 DB에 저장 : socialType, socialId, email, role 값만 있는 상태
     */
    private fun saveUser(attributes: OAuthAttributes, socialType: SocialType): User {
        val createdUser: User = attributes.toEntity(socialType, attributes.getOauth2UserInfo())
        return userRepository!!.save(createdUser)
    }

    companion object {
        private const val NAVER = "naver"
        private const val KAKAO = "kakao"
    }
}