package com.edoctor.api.chat

import com.google.gson.Gson
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import com.edoctor.api.chat.TextMessage as ChatTextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.lang.StringBuilder
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatHandler : TextWebSocketHandler() {

    val log = KotlinLogging.logger { }

    // TODO: synchronize
    var chatSessions: Map<String, List<WebSocketSession>> = ConcurrentHashMap()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            log.info { "handleTextMessage(chatSessions=${chatSessionsLogInfo()})" }

            session.sendMessageIfOpened(message)
            log.info { "handleTextMessage sendToSender(email=${session.email}, ${message.payload})" }

            val chatMessage = Gson().fromJson(message.payload, ChatTextMessage::class.java)
            chatSessions[chatMessage.recipientEmail]?.forEach {
                it.sendMessageIfOpened(message)
                log.info { "handleTextMessage sendToReceiver(email=${it.email}, ${message.payload})" }
            }
        } catch (e: Exception) {
            log.info { "handleTextMessage error(email=${session.email}: $e" }
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        session.email?.let {
            val list = chatSessions[it]
            if (list == null) {
                chatSessions += it to listOf(session)
            } else {
                chatSessions += it to (list + session)
            }
        }

        log.info { "afterConnectionEstablished(email=${session.email}, chatSessions=${chatSessionsLogInfo()})" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        session.email?.let {
            val list = chatSessions[it]
            if (list != null) {
                val afterRemove = list - session
                if (afterRemove.isEmpty()) {
                    chatSessions -= it
                } else {
                    chatSessions += it to afterRemove
                }
            }
        }

        log.info { "afterConnectionClosed(email=${session.email}, chatSessions=${chatSessionsLogInfo()})" }
    }

    private fun chatSessionsLogInfo(): String {
        val builder = StringBuilder("[")
         chatSessions.keys.forEach {
            builder.append((it to (chatSessions[it]?.size ?: 0)).toString())
        }
        builder.append("]")
        return builder.toString()
    }

    private fun WebSocketSession?.sendMessageIfOpened(message: TextMessage) {
        this?.takeIf { it.isOpen }?.sendMessage(message)
    }

    private val WebSocketSession.email: String?
        get() = this.principal?.name

}