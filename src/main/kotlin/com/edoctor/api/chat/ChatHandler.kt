package com.edoctor.api.chat

import mu.KotlinLogging
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import com.edoctor.api.chat.TextMessage as ChatTextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatHandler : TextWebSocketHandler() {

    val log = KotlinLogging.logger { }

    // TODO: synchronize
    var chatSessions: Map<String, WebSocketSession> = hashMapOf()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        log.info { "handleTextMessage(principal=${session.principal}, $message)" }

        try {
            session.sendMessageIfOpened(message)

            val chatMessage = ObjectMapper().readValue(message.payload, ChatTextMessage::class.java)
            chatSessions[chatMessage.recipientEmail].sendMessageIfOpened(message)
        } catch (e: Exception) {
            log.info { "handleTextMessage error(principal=${session.principal}: $e" }
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info { "afterConnectionEstablished(principal=${session.principal}, chatSessions=$chatSessions)" }

        session.email?.let { chatSessions += it to session }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        log.info { "afterConnectionClosed(principal=${session.principal}, chatSessions=$chatSessions)" }

        session.email?.let { chatSessions -= it }
    }

    private fun WebSocketSession?.sendMessageIfOpened(message: TextMessage) {
        this?.takeIf { it.isOpen }?.sendMessage(message)
    }

    private val WebSocketSession.email: String?
        get() = this.principal?.name

}