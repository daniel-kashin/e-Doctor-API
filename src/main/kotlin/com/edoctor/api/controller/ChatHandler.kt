package com.edoctor.api.controller

import com.edoctor.api.configuration.socket.WebSocketPrincipal
import com.edoctor.api.entities.storage.Conversation
import com.edoctor.api.entities.storage.Doctor
import com.edoctor.api.entities.storage.Message
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
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
    private lateinit var conversationRepository: ConversationRepository

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    var chatSessions: Map<String, MutableList<WebSocketSession>> = ConcurrentHashMap()

    @Transactional
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            session.webSocketPrincipal?.let { principal ->
                val chatMessage = Gson().fromJson(message.payload, ChatTextMessage::class.java)

                val patientUuid = if (principal.isPatient) principal.uuid else chatMessage.recipientUuid
                val doctorUuid = if (principal.isPatient) chatMessage.recipientUuid else principal.name

                val conversation = conversationRepository.findByPatientUuidAndDoctorUuid(patientUuid, principal.name)
                        ?: run {
                            val patient = patientRepository.findById(patientUuid).orElse(null) ?: return@let
                            val doctor = doctorRepository.findByEmail(doctorUuid) ?: return@let
                            Conversation(patient, doctor, mutableSetOf())
                        }

                conversation.messages.add(
                        Message(System.currentTimeMillis() / 1000, chatMessage.text, principal.isPatient, conversation)
                )
                conversationRepository.save(conversation)

                session.sendMessageIfOpened(message)

                chatSessions[chatMessage.recipientUuid]?.forEach {
                    it.sendMessageIfOpened(message)
                }
            }
        } catch (e: Exception) {
            // do nothing
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        session.email?.let { email ->
            val list = chatSessions[email] ?: mutableListOf()
            chatSessions += email to list.apply { add(session) }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        session.email?.let { email ->
            val afterRemove = chatSessions[email]?.apply { remove(session) } ?: mutableListOf()
            if (afterRemove.isEmpty()) {
                chatSessions -= email
            } else {
                chatSessions += email to afterRemove
            }
        }
    }

    private fun WebSocketSession?.sendMessageIfOpened(message: TextMessage) =
            this?.takeIf { it.isOpen }?.sendMessage(message)

    private val WebSocketSession.email: String?
        get() = this.principal?.name

    private val WebSocketSession.webSocketPrincipal: WebSocketPrincipal?
        get() = this.principal as? WebSocketPrincipal

}