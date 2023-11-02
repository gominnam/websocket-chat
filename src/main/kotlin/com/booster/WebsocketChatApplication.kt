package com.booster

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableWebSecurity
@EnableJpaAuditing
class WebsocketChatApplication

fun main(args: Array<String>) {
    runApplication<WebsocketChatApplication>(*args)
}
