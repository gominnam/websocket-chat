package com.booster.config.login

import com.booster.entity.User
import com.booster.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class LoginService(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String?): CustomUserDetails {
        if(email == null) {
            throw UsernameNotFoundException("이메일을 입력해주세요.")
        }
        val user: User = userRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("해당 이메일이 존재하지 않습니다.") }

        return CustomUserDetails.Builder()
            .email(user.email)
            .password(user.password)
            .name(user.name)
            .authorities(user.role)//CusteomUserDetails 수정?
            .build()
    }

}