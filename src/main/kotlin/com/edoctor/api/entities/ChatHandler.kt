package com.edoctor.api.entities

import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatHandler : TextWebSocketHandler() {

    val log = KotlinLogging.logger { }

    var sessions: List<WebSocketSession> = ArrayList()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        log.info { "handleTextMessage($session, $message)" }
        sessions.forEach { it.sendMessage(message) }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions += session
        log.info { "afterConnectionEstablished(sessions=$sessions)" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions -= session
        log.info { "afterConnectionClosed(sessions=$sessions)" }
    }
}