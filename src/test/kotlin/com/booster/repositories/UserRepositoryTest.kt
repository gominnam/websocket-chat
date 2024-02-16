package com.booster.repositories

import com.booster.entity.User
import com.booster.enums.Role
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@RunWith(MockitoJUnitRunner::class)
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `it should find user by email and password`() {
        //given
        val user = User(null, "email", "", "password", "", Role.USER, null, "", "")
        testEntityManager.persistAndFlush(user)

        //when
        val optionalUser = userRepository.login(user.email, user.password)

        //Then
        assertThat(optionalUser).isPresent
        val expectedUser = optionalUser.get()
        assertThat(expectedUser.id).isNotNull()
        assertThat(expectedUser.email).isEqualTo("email")
        assertThat(expectedUser.password).isEqualTo("password")
    }
}