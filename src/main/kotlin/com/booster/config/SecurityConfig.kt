package com.booster.config

import com.booster.config.jwt.JwtService
import com.booster.config.jwt.filter.JwtTokenAuthenticationFilter
import com.booster.config.login.CustomUserDetailsService
import com.booster.config.login.filter.CustomJsonUsernamePasswordAuthenticationFilter
import com.booster.config.login.handler.LoginFailureHandler
import com.booster.config.login.handler.LoginSuccessHandler
import com.booster.config.oauth.CustomOAuth2UserService
import com.booster.config.oauth.handler.CustomOAuth2LoginFailureHandler
import com.booster.config.oauth.handler.CustomOAuth2LoginSuccessHandler
import com.booster.repositories.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(
    val userRepository: UserRepository,
    val jwtService: JwtService,
    val customUserDetailsService: CustomUserDetailsService,
    val customOAuth2UserService: CustomOAuth2UserService,
    val objectMapper: ObjectMapper,
    val corsConfig: CorsConfig
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun customOAuth2LoginSuccessHandler(): AuthenticationSuccessHandler {
        return CustomOAuth2LoginSuccessHandler(jwtService, userRepository)
    }

    @Bean
    fun customOAuth2LoginFailureHandler(): AuthenticationFailureHandler {
        return CustomOAuth2LoginFailureHandler()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager? {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder())
        provider.setUserDetailsService(customUserDetailsService)
        return ProviderManager(listOf(provider))
    }

    @Bean
    fun loginSuccessHandler(): LoginSuccessHandler {
        return LoginSuccessHandler(jwtService, userRepository)
    }

    @Bean
    fun loginFailureHandler(): LoginFailureHandler {
        return LoginFailureHandler()
    }

    @Bean
    fun customJsonUsernamePasswordAuthenticationFilter(): CustomJsonUsernamePasswordAuthenticationFilter? {
        val customJsonUsernamePasswordLoginFilter = CustomJsonUsernamePasswordAuthenticationFilter(objectMapper)
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager())
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler())
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler())
        return customJsonUsernamePasswordLoginFilter
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain {
        http
            .addFilter(corsConfig.corsFilter())
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(*SecurityConfigProperties.NO_CHECK_URLS.toTypedArray()).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { oauth2Login ->
                oauth2Login
                    .successHandler(customOAuth2LoginSuccessHandler())
                    .failureHandler(customOAuth2LoginFailureHandler())
                    .userInfoEndpoint { it.userService(customOAuth2UserService) }
            }
            .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter::class.java)
            .addFilterBefore(JwtTokenAuthenticationFilter(jwtService, customUserDetailsService, userRepository), CustomJsonUsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
