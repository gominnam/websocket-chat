package com.booster.controller

import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@RunWith(MockitoJUnitRunner::class)
@WebMvcTest(GreetingController::class, excludeAutoConfiguration = [SecurityAutoConfiguration::class])
class AuthControllerTest {

}