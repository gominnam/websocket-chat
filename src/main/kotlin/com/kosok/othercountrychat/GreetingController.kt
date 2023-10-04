package com.kosok.othercountrychat

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils

@Controller
class GreetingController {
    @MessageMapping("/entry")
    @SendTo("/topic/greetings")
    fun greeting(message: Message): Message {
        return Message(HtmlUtils.htmlEscape(message.sender), HtmlUtils.htmlEscape(message.content))
    }

    @MessageMapping("/message")
    @SendTo("/topic/greetings")
    fun send(message: Message): Message {
        return Message(HtmlUtils.htmlEscape(message.sender + ": "), HtmlUtils.htmlEscape(message.content))
    }
}
