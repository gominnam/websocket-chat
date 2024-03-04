package com.booster.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController {
    @GetMapping("/", "/signup", "/chat")
    fun index(): String {
        return "index"
    }

    @GetMapping("/components/main")
    fun main(): String {
        return "components/main"
    }

    @GetMapping("/components/error")
    fun error(): String {
        return "components/error"
    }

    @GetMapping("/components/chat")
    fun chat(): String {
        return "components/chat"
    }

    @GetMapping("/components/signup")
    fun signup(): String {
        return "components/signup"
    }
}