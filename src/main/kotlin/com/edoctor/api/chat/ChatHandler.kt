package com.edoctor.api.chat

import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatHandler : TextWebSocketHandler() {

    val log = KotlinLogging.logger { }

    var chatSessions: List<WebSocketSession> = ArrayList()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        log.info { "handleTextMessage($session, $message)" }
        chatSessions.forEach { it.sendMessage(message) }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        chatSessions += session
        log.info { "afterConnectionEstablished(chatSessions=$chatSessions)" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        chatSessions -= session
        log.info { "afterConnectionClosed(chatSessions=$chatSessions)" }
    }

}