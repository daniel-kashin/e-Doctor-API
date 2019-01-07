package com.edoctor.api.entities

import mu.KotlinLogging
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.*
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor

@Configuration
@EnableWebSocket
class WebSocketConfiguration : WebSocketConfigurer {

    val log = KotlinLogging.logger { }

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        log.info { "registerWebSocketHandlers()" }
        registry.addHandler(ChatHandler(), "/chat")
                .setAllowedOrigins("*")
                .addInterceptors(HttpSessionHandshakeInterceptor())
    }

}