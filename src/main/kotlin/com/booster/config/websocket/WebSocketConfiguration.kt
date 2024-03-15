package com.booster.config.websocket

import com.booster.config.jwt.JwtService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import java.security.Principal


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfiguration : WebSocketMessageBrokerConfigurer {

    @Autowired
    lateinit var jwtService: JwtService

    val log = KotlinLogging.logger {}

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocket-chat")
            .setAllowedOrigins("http://localhost:8080", "https://boosterko.kr")  // Change in production: "https://boosterko.kr"
            .withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic", "/queue")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
                val accessor = StompHeaderAccessor.wrap(message)
                log.info { "Command: ${accessor.command}" }
                if (accessor.command == StompCommand.CONNECT) {
                    val authToken = accessor.getFirstNativeHeader("Authorization")?.substringAfter("Bearer ")
                    if (authToken == null || !jwtService.isTokenValid(authToken)) {
                        log.info { "Invalid or missing auth token" }
                        throw IllegalArgumentException("Invalid or missing auth token")
                    }
                    val email = jwtService.extractEmail(authToken)
                    val userPrincipal = UserPrincipal(email!!)
                    SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(userPrincipal, null, null)
                }
                return message
            }
        })
    }

}

data class UserPrincipal(val username: String) : Principal {
    override fun getName(): String = username
}