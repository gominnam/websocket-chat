package com.kosok.othercountrychat

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils

@Controller
class GreetingController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    fun greeting(user: User): User {
        return User("Hello, " + HtmlUtils.htmlEscape(user.name))
    }
}
