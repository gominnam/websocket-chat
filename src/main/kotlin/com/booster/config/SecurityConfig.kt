package com.booster.config

import com.booster.config.jwt.JwtAuthFilter
import com.booster.config.jwt.JwtService
import com.booster.config.login.LoginService
import com.booster.config.login.filter.CustomJsonUsernamePasswordAuthenticationFilter
import com.booster.config.login.handler.LoginFailureHandler
import com.booster.config.login.handler.LoginSuccessHandler
import com.booster.config.oauth.CustomOAuth2UserService
import com.booster.config.oauth.handler.OAuth2FailureHandler
import com.booster.config.oauth.handler.OAuth2SuccessHandler
import com.booster.repositories.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(val userRepository: UserRepository, val jwtService: JwtService, val loginService: LoginService,
                     val oAuth2SuccessHandler: OAuth2SuccessHandler, val oAuth2FailureHandler: OAuth2FailureHandler,
                     val customOAuth2UserService: CustomOAuth2UserService) {

    private val objectMapper: ObjectMapper = ObjectMapper()

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(
                "/",
                "/css/**",
                "/images/**",
                "/js/**",
                "/api/user/register",
            )
        }
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .formLogin { formLogin -> formLogin.disable() }
            .httpBasic { httpBasic -> httpBasic.disable() }
            .csrf { csrf -> csrf.disable() }
            .headers { headers ->  headers.frameOptions().disable() }
            .sessionManagement { sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/api/user/login").permitAll()
                    .requestMatchers("/chat").hasRole("USER")
                    .anyRequest()
                    .authenticated()
            }
            .oauth2Login { oauth2Login ->
                oauth2Login
                    .successHandler(oAuth2SuccessHandler)
                    .failureHandler(oAuth2FailureHandler)
                    .userInfoEndpoint { userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserService) }
            }

        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter::class.java)
        http.addFilterBefore(jwtAuthFilter(), CustomJsonUsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder())
        provider.setUserDetailsService(loginService)
        return ProviderManager(provider)
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
    fun customJsonUsernamePasswordAuthenticationFilter(): CustomJsonUsernamePasswordAuthenticationFilter {
        val loginFilter = CustomJsonUsernamePasswordAuthenticationFilter(objectMapper)
        loginFilter.setAuthenticationManager(authenticationManager())
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler())
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler())
        return loginFilter
    }

    @Bean
    fun jwtAuthFilter(): JwtAuthFilter {
        return JwtAuthFilter(jwtService, userRepository)
    }
}