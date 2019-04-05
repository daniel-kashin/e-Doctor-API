package com.edoctor.api.controller

import com.edoctor.api.configuration.socket.WebSocketPrincipal
import com.edoctor.api.entities.domain.CallActionRequest
import com.edoctor.api.entities.domain.CallStatusResponse
import com.edoctor.api.entities.network.request.CallActionMessageRequest
import com.edoctor.api.entities.network.request.MessageRequestWrapper
import com.edoctor.api.entities.network.request.TextMessageRequest
import com.edoctor.api.entities.storage.ConversationEntity
import com.edoctor.api.mapper.MessageMapper.toDomainCallAction
import com.edoctor.api.mapper.MessageMapper.toEntityCallAction
import com.edoctor.api.mapper.MessageMapper.toEntityText
import com.edoctor.api.mapper.MessageMapper.toResponse
import com.edoctor.api.mapper.MessageMapper.unwrapRequest
import com.edoctor.api.mapper.MessageMapper.wrapResponse
import com.edoctor.api.repositories.CallRepository
import com.edoctor.api.repositories.ConversationRepository
import com.edoctor.api.repositories.DoctorRepository
import com.edoctor.api.repositories.PatientRepository
import com.edoctor.api.service.ConversationService
import com.google.gson.Gson
import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import com.edoctor.api.entities.network.response.TextMessageResponse as ChatTextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatHandler : TextWebSocketHandler() {

    val log = logger { }

    @Autowired
    private lateinit var conversationService: ConversationService

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var doctorRepository: DoctorRepository

    @Autowired
    private lateinit var callRepository: CallRepository

    var chatSessions: Map<WebSocketPrincipal, MutableList<WebSocketSession>> = ConcurrentHashMap()

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            session.webSocketPrincipal?.let { principal ->
                handleMessage(message, principal)
            }
        } catch (e: Exception) {
            log.info { "handleTextMessage($e)" }
        }
    }

    @Transactional
    fun handleMessage(
            requestSocketMessage: TextMessage,
            principal: WebSocketPrincipal
    ): Boolean {
        val conversationEntity = conversationService.getConversation(principal.email, principal.recipientEmail, principal.isPatient)
                ?: return false

        log.info { "got conversation" }

        val messageWrapper = Gson().fromJson(requestSocketMessage.payload, MessageRequestWrapper::class.java)
        val messageRequest = unwrapRequest(messageWrapper)

        log.info { "unwrap request: messageRequest" }

        when (messageRequest) {
            is TextMessageRequest -> {
                val messageEntity = toEntityText(messageRequest, principal.isPatient, conversationEntity)
                val patientEntity = patientRepository.findByEmail(principal.patientEmail)
                val doctorEntity = doctorRepository.findByEmail(principal.doctorEmail)

                conversationService.addMessage(messageEntity, conversationEntity)

                val messageResponse = wrapResponse(toResponse(messageEntity, patientEntity!!, doctorEntity!!))
                val responseSocketMessage = TextMessage(Gson().toJson(messageResponse))

                chatSessions.entries
                        .filter { principal.isInTheSameConversation(it.key) }
                        .forEach { (_, sessions) ->
                            sessions.forEach { it.sendMessageIfOpened(responseSocketMessage) }
                        }
            }
            is CallActionMessageRequest -> {
                val callActionRequest = toDomainCallAction(messageRequest)
                val callStatusResponse = callRepository.onCallActionRequest(
                        callActionRequest,
                        principal.patientEmail,
                        principal.doctorEmail,
                        principal.isPatient
                ) ?: return false

                saveAndSendCallStatusResponse(principal, conversationEntity, callStatusResponse)
            }
        }

        return true
    }

    fun saveAndSendCallStatusResponse(
            principal: WebSocketPrincipal,
            conversationEntity: ConversationEntity,
            callStatusResponse: CallStatusResponse
    ) {
        val messageEntity = toEntityCallAction(callStatusResponse, conversationEntity)
        val patientEntity = patientRepository.findByEmail(principal.patientEmail)
        val doctorEntity = doctorRepository.findByEmail(principal.doctorEmail)

        conversationService.addMessage(messageEntity, conversationEntity)

        val messageResponse = wrapResponse(toResponse(messageEntity, patientEntity!!, doctorEntity!!))
        val responseSocketMessage = TextMessage(Gson().toJson(messageResponse))

        chatSessions.entries
                .filter { principal.isInTheSameConversation(it.key) }
                .forEach { (_, sessions) ->
                    sessions.forEach { it.sendMessageIfOpened(responseSocketMessage) }
                }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        session.webSocketPrincipal?.let { principal ->
            val list = chatSessions[principal] ?: mutableListOf()
            chatSessions += principal to list.apply { add(session) }
        }
        log.info { "afterConnectionEstablished(email=${session.email}, chatSessions=${chatSessionsLogInfo()})" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        session.webSocketPrincipal?.let { principal ->
            val afterRemove = chatSessions[principal]?.apply { remove(session) } ?: mutableListOf()
            if (afterRemove.isEmpty()) {
                chatSessions -= principal
            } else {
                chatSessions += principal to afterRemove
            }

            callRepository.findActiveCall(principal.patientEmail, principal.doctorEmail)?.let { call ->
                val callStatusResponse = callRepository.onCallActionRequest(call, CallActionRequest.CallAction.LEAVE)
                        ?: return

                val conversationEntity = conversationService.getConversation(principal.email, principal.recipientEmail, principal.isPatient)
                        ?: return

                saveAndSendCallStatusResponse(principal, conversationEntity, callStatusResponse)
            }
        }
        log.info { "afterConnectionClosed(email=${session.email}, chatSessions=${chatSessionsLogInfo()})" }
    }

    //region utils

    private fun WebSocketPrincipal.isInTheSameConversation(principal: WebSocketPrincipal): Boolean {
        return principal.email == email && principal.recipientEmail == recipientEmail
                || principal.email == recipientEmail && principal.recipientEmail == email
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

    //endregion

}