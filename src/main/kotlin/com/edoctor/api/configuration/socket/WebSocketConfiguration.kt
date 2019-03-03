package com.edoctor.api.configuration.socket

import com.edoctor.api.controller.ChatHandler
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.*
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor
import java.security.Principal

@Configuration
@EnableWebSocket
class WebSocketConfiguration : WebSocketConfigurer {

    companion object {
        private const val RECIPIENT_EMAIL_HEADER = "recipient-email"
    }

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var chatHandler: ChatHandler

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(chatHandler, "/chat")
                .setAllowedOrigins("*")
                .addInterceptors(HttpSessionHandshakeInterceptor())
                .setHandshakeHandler(
                        object : DefaultHandshakeHandler() {
                            override fun determineUser(request: ServerHttpRequest, wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>): Principal? {
                                val user = super.determineUser(request, wsHandler, attributes) ?: return null
                                val recipientEmail = request.headers[RECIPIENT_EMAIL_HEADER]?.firstOrNull() ?: return null

                                val doctor = doctorRepository.findByEmail(user.name)
                                if (doctor != null) return WebSocketPrincipal(user.name, recipientEmail, false)

                                val patient = patientRepository.findByEmail(user.name)
                                if (patient != null) return WebSocketPrincipal(user.name, recipientEmail, true)

                                return null
                            }
                        }
                )
    }

}