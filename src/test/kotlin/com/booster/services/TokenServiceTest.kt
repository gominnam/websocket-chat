package com.booster.services

import com.booster.dto.UserDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.security.oauth2.jwt.*

@RunWith(MockitoJUnitRunner::class)
@DataJpaTest
class TokenServiceTest {
    @InjectMocks
    private lateinit var tokenService: TokenService

    @Mock
    private lateinit var jwtDecoder: JwtDecoder

    @Mock
    private lateinit var jwtEncoder: JwtEncoder

    @Mock
    private lateinit var userService: UserService

    @Before
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `it should create token with given userDto`() {
        val userDTO =  UserDTO(1, "email", "name", "password")
        val jwsHeader = JwsHeader.with { "HS256" }.build()
//        val claims = JwtClaimsSet.builder()
//            .issuedAt(Instant.now())
//            .expiresAt(Instant.now().plus(30L, ChronoUnit.DAYS))
//            .subject(userDTO.name)
//            .claim("userId", userDTO.email)
//            .build()

        given(jwtEncoder.encode(any()).tokenValue).willReturn("access_token")
        //when
        val result = tokenService.createToken(userDTO)

        //Then
        assertThat(result).isEqualTo("access_token")
    }
}