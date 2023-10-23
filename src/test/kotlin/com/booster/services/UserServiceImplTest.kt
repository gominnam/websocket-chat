package com.booster.services

import com.booster.dto.UserDTO
import com.booster.entity.User
import com.booster.exception.ErrorCode
import com.booster.exception.UserException
import com.booster.repositories.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.modelmapper.ModelMapper
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.*

@RunWith(MockitoJUnitRunner::class)
@DataJpaTest
class UserServiceImplTest {
    @InjectMocks
    private lateinit var userService: UserServiceImpl

    @Mock
    private lateinit var userRepository: UserRepository

    @Spy
    private lateinit var modelMapper: ModelMapper

    @Before
    fun init() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `it should throw user already exists with given email`() {
        val user = User(1L, email="booster@naver.com", name="name", password="booster")
        val userDTO = UserDTO(null, email="booster@naver.com", name="name", password="booster")

        given(userRepository.existsByEmail(user.email)).willReturn(true)

        //when
        val throwable = catchThrowable { userService.createUser(userDTO) }

        //then
        assertThat(throwable).isNotNull()
        assertThat(throwable).isInstanceOf(UserException::class.java)
        assertThat(throwable.message).isEqualTo(ErrorCode.USER_ALREADY_EXISTS.description)
        verify(userRepository).existsByEmail(user.email)
    }

    @Test
    fun `it should create user with given email and name and password`(){
        val savedUser = User(1L, email="booster@naver.com", name="name", password="booster")

        given(userRepository.save(any(User::class.java))).willReturn(savedUser)

        //when
        val userDTO = UserDTO(null, email="booster@naver.com", name="name", password="booster")
        val createdUser = userService.createUser(userDTO)

        //then
        assertThat(createdUser).isNotNull()
        assertThat(createdUser).isInstanceOf(UserDTO::class.java)
        assertThat(createdUser?.email).isEqualTo("booster@naver.com")
        assertThat(createdUser?.name).isEqualTo("name")
        assertThat(createdUser?.password).isEqualTo("booster")
        verify(userRepository).save(any(User::class.java))
    }

    @Test
    fun `it should throw not found user with given email and password`() {
        given(userRepository.login("email", "password")).willReturn(Optional.empty())

        //when
        val userDTO = UserDTO(null, email="email", "", password="password")
        val throwable = catchThrowable { userService.login(userDTO) }

        //then
        assertThat(throwable).isNotNull()
        assertThat(throwable).isInstanceOf(UserException::class.java)
        assertThat(throwable.message).isEqualTo(ErrorCode.USER_NOT_FOUND.description)
    }

    @Test
    fun `it should get user with given email and password`(){
        val user = User(1L, email="email", name="name", password="password")

        given(userRepository.login("email", "password")).willReturn(Optional.of(user))

        //when
        val userDTO = UserDTO(null, email="email", "", password="password")
        val loginUser = userService.login(userDTO)

        //then
        assertThat(loginUser).isNotNull()
        assertThat(loginUser).isInstanceOf(UserDTO::class.java)
        assertThat(loginUser?.email).isEqualTo("email")
        assertThat(loginUser?.name).isEqualTo("name")
        assertThat(loginUser?.password).isEqualTo("password")
    }

    @Test
    fun `it should get user with given id`(){
        val user = User(1L, email="email", name="name", password="password")

        given(userRepository.findById(1L)).willReturn(Optional.of(user))

        //when
        val findUser = userService.findById(1L)

        //then
        assertThat(findUser).isNotNull()
        assert(findUser.id == 1L)
        assertThat(findUser.email).isEqualTo("email")
        assertThat(findUser.name).isEqualTo("name")
        assertThat(findUser.password).isEqualTo("password")
    }

}