package com.booster

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class WebsocketChatApplication

fun main(args: Array<String>) {
    runApplication<WebsocketChatApplication>(*args)
}
