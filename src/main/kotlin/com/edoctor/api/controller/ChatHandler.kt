package com.edoctor.api.controller

import com.edoctor.api.configuration.socket.WebSocketPrincipal
import com.edoctor.api.entities.storage.Conversation
import com.edoctor.api.entities.storage.Message
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import com.edoctor.api.service.ConversationService
import com.google.gson.Gson
import mu.KotlinLogging.logger
import org.hibernate.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import com.edoctor.api.entities.network.TextMessage as ChatTextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import javax.transaction.Transactional

@Component
class ChatHandler : TextWebSocketHandler() {

    @Autowired
    private lateinit var conversationService: ConversationService

    val log = logger { }

    var chatSessions: Map<String, MutableList<WebSocketSession>> = ConcurrentHashMap()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            session.webSocketPrincipal?.let { principal ->
                val chatMessage = Gson().fromJson(message.payload, ChatTextMessage::class.java)

                conversationService.addMessage(principal.email, principal.isPatient, chatMessage)

                session.sendMessageIfOpened(message)

                chatSessions[chatMessage.recipientEmail]?.forEach {
                    it.sendMessageIfOpened(message)
                }
            }
        } catch (e: Exception) {
            log.info { "handleTextMessage($e)" }
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        session.email?.let { email ->
            val list = chatSessions[email] ?: mutableListOf()
            chatSessions += email to list.apply { add(session) }
        }
        log.info { "afterConnectionEstablished(email=${session.email}, chatSessions=${chatSessionsLogInfo()})" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        // TODO: replace with uuid
        session.email?.let { email ->
            val afterRemove = chatSessions[email]?.apply { remove(session) } ?: mutableListOf()
            if (afterRemove.isEmpty()) {
                chatSessions -= email
            } else {
                chatSessions += email to afterRemove
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

    private fun WebSocketSession?.sendMessageIfOpened(message: TextMessage) =
            this?.takeIf { it.isOpen }?.sendMessage(message)

    private val WebSocketSession.email: String?
        get() = this.principal?.name

    private val WebSocketSession.webSocketPrincipal: WebSocketPrincipal?
        get() = this.principal as? WebSocketPrincipal

}