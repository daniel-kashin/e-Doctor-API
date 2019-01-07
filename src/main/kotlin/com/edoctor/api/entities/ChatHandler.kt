package com.edoctor.api.entities

import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class ChatHandler : TextWebSocketHandler() {

    var sessions: List<WebSocketSession> = ArrayList()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        sessions.forEach { it.sendMessage(message) }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions += session
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions -= session
    }
}