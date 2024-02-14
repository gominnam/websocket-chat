package com.booster.config

import com.booster.config.jwt.JwtService
import com.booster.config.jwt.JwtTokenAuthenticationFilter
import com.booster.repositories.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(val userRepository: UserRepository, val jwtService: JwtService) {

    //private val objectMapper: ObjectMapper = ObjectMapper()

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun jwtTokenAuthenticationFilter(): JwtTokenAuthenticationFilter {
        return JwtTokenAuthenticationFilter(jwtService, userRepository)
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(
                "/",
                "/css/**",
                "/images/**",
                "/js/**",
                "/api/user/login",
                "/api/user/register",
            )
        }
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .formLogin { formLogin -> formLogin.disable() }
            .httpBasic { httpBasic -> httpBasic.disable() }
            .logout { logout ->
                logout.permitAll()
            }
            .oauth2Login { oauth2Login ->
                oauth2Login.defaultSuccessUrl("/chat")
            }
            .addFilterBefore(JwtTokenAuthenticationFilter(jwtService, userRepository), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

}