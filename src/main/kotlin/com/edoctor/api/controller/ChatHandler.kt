package com.edoctor.api.controller

import com.edoctor.api.configuration.socket.WebSocketPrincipal
import com.edoctor.api.entities.domain.CallActionRequest
import com.edoctor.api.entities.domain.CallStatusResponse
import com.edoctor.api.entities.network.request.CallActionMessageRequest
import com.edoctor.api.entities.network.request.MessageRequestWrapper
import com.edoctor.api.entities.network.request.TextMessageRequest
import com.edoctor.api.entities.storage.ConversationEntity
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.mapper.MessageMapper.toDomainCallAction
import com.edoctor.api.mapper.MessageMapper.toEntityCallAction
import com.edoctor.api.mapper.MessageMapper.toEntityImage
import com.edoctor.api.mapper.MessageMapper.toEntityMedicalAccesses
import com.edoctor.api.mapper.MessageMapper.toEntityMedicalRecordRequest
import com.edoctor.api.mapper.MessageMapper.toEntityText
import com.edoctor.api.mapper.MessageMapper.toResponse
import com.edoctor.api.mapper.MessageMapper.unwrapRequest
import com.edoctor.api.mapper.MessageMapper.wrapResponse
import com.edoctor.api.repositories.CallRepository
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

    override fun afterConnectionEstablished(session: WebSocketSession) {
        session.webSocketPrincipal?.let { principal ->
            val list = chatSessions[principal] ?: mutableListOf()
            chatSessions += principal to list.apply { add(session) }
        }
        log.info { "afterConnectionEstablished(email=${session.uuid}, chatSessions=${chatSessionsLogInfo()})" }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        session.webSocketPrincipal?.let { principal ->
            val afterRemove = chatSessions[principal]?.apply { remove(session) } ?: mutableListOf()
            if (afterRemove.isEmpty()) {
                chatSessions -= principal
            } else {
                chatSessions += principal to afterRemove
            }

            callRepository.findActiveCall(principal.patientUuid, principal.doctorUuid)?.let { call ->
                val callStatusResponse = callRepository.onCallActionRequest(call, CallActionRequest.CallAction.LEAVE)
                        ?: return

                val conversationEntity = conversationService.getConversation(principal.uuid, principal.recipientUuid, principal.isPatient)
                        ?: return

                saveAndSendCallStatusResponse(principal, conversationEntity, callStatusResponse)
            }
        }
        log.info { "afterConnectionClosed(email=${session.uuid}, chatSessions=${chatSessionsLogInfo()})" }
    }

    fun onImageUploaded(principal: WebSocketPrincipal, imageUuid: String) {
        val conversationEntity = conversationService.getConversation(principal.uuid, principal.recipientUuid, principal.isPatient)
                ?: return
        val patientEntity = patientRepository.findById(principal.patientUuid).orElse(null) ?: return
        val doctorEntity = doctorRepository.findById(principal.doctorUuid).orElse(null) ?: return

        val messageEntity = toEntityImage(imageUuid, principal.isPatient, conversationEntity)

        conversationService.addMessage(messageEntity, conversationEntity)

        val messageResponse = wrapResponse(toResponse(messageEntity, patientEntity, doctorEntity))
        val responseSocketMessage = TextMessage(Gson().toJson(messageResponse))

        chatSessions.entries
                .filter { it.key.isInTheSameConversation(principal) }
                .forEach { (_, sessions) ->
                    sessions.forEach { it.sendMessageIfOpened(responseSocketMessage) }
                }
    }

    fun onMedicalAccessesChanged(patientEntity: PatientEntity, doctorEntity: DoctorEntity) {
        val conversationEntity = conversationService.getConversation(patientEntity.email, doctorEntity.email, true) ?: return

        val messageEntity = toEntityMedicalAccesses(conversationEntity)

        conversationService.addMessage(messageEntity, conversationEntity)

        val messageResponse = wrapResponse(toResponse(messageEntity, patientEntity, doctorEntity))
        val responseSocketMessage = TextMessage(Gson().toJson(messageResponse))

        chatSessions.entries
                .filter { it.key.isInTheSameConversation(doctorEntity.uuid, patientEntity.uuid) }
                .forEach { (_, sessions) ->
                    sessions.forEach { it.sendMessageIfOpened(responseSocketMessage) }
                }
    }

    fun onMedicalRecordRequest(patientEntity: PatientEntity, doctorEntity: DoctorEntity) {
        val conversationEntity = conversationService.getConversation(patientEntity.email, doctorEntity.email, true) ?: return

        val messageEntity = toEntityMedicalRecordRequest(conversationEntity)

        conversationService.addMessage(messageEntity, conversationEntity)

        val messageResponse = wrapResponse(toResponse(messageEntity, patientEntity, doctorEntity))
        val responseSocketMessage = TextMessage(Gson().toJson(messageResponse))

        chatSessions.entries
                .filter { it.key.isInTheSameConversation(doctorEntity.uuid, patientEntity.uuid) }
                .forEach { (_, sessions) ->
                    sessions.forEach { it.sendMessageIfOpened(responseSocketMessage) }
                }
    }

    @Transactional
    fun handleMessage(
            requestSocketMessage: TextMessage,
            principal: WebSocketPrincipal
    ): Boolean {
        val conversationEntity = conversationService.getConversation(principal.uuid, principal.recipientUuid, principal.isPatient)
                ?: return false

        val messageWrapper = Gson().fromJson(requestSocketMessage.payload, MessageRequestWrapper::class.java)
        val messageRequest = unwrapRequest(messageWrapper)

        when (messageRequest) {
            is TextMessageRequest -> {
                val messageEntity = toEntityText(messageRequest, principal.isPatient, conversationEntity)
                val patientEntity = patientRepository.findById(principal.patientUuid).orElse(null) ?: return false
                val doctorEntity = doctorRepository.findById(principal.doctorUuid).orElse(null) ?: return false

                conversationService.addMessage(messageEntity, conversationEntity)

                val messageResponse = wrapResponse(toResponse(messageEntity, patientEntity, doctorEntity))
                val responseSocketMessage = TextMessage(Gson().toJson(messageResponse))

                chatSessions.entries
                        .filter { it.key.isInTheSameConversation(principal) }
                        .forEach { (_, sessions) ->
                            sessions.forEach { it.sendMessageIfOpened(responseSocketMessage) }
                        }
            }
            is CallActionMessageRequest -> {
                val callActionRequest = toDomainCallAction(messageRequest)
                val callStatusResponse = callRepository.onCallActionRequest(
                        callActionRequest,
                        principal.patientUuid,
                        principal.doctorUuid,
                        principal.isPatient
                ) ?: return false

                saveAndSendCallStatusResponse(principal, conversationEntity, callStatusResponse)
            }
        }

        return true
    }

    private fun saveAndSendCallStatusResponse(
            principal: WebSocketPrincipal,
            conversationEntity: ConversationEntity,
            callStatusResponse: CallStatusResponse
    ) {
        val messageEntity = toEntityCallAction(callStatusResponse, conversationEntity)
        val patientEntity = patientRepository.findById(principal.patientUuid).orElse(null) ?: return
        val doctorEntity = doctorRepository.findById(principal.doctorUuid).orElse(null) ?: return

        conversationService.addMessage(messageEntity, conversationEntity)

        val messageResponse = wrapResponse(toResponse(messageEntity, patientEntity, doctorEntity))
        val responseSocketMessage = TextMessage(Gson().toJson(messageResponse))

        chatSessions.entries
                .filter { it.key.isInTheSameConversation(principal) }
                .forEach { (_, sessions) ->
                    sessions.forEach { it.sendMessageIfOpened(responseSocketMessage) }
                }
    }

    //region utils

    private fun WebSocketPrincipal.isInTheSameConversation(principal: WebSocketPrincipal): Boolean {
        return principal.uuid == uuid && principal.recipientUuid == recipientUuid
                || principal.uuid == recipientUuid && principal.recipientUuid == uuid
    }

    private fun WebSocketPrincipal.isInTheSameConversation(doctorUuid: String, patientUuid: String): Boolean {
        return doctorUuid == uuid && patientUuid == recipientUuid
                || doctorUuid == recipientUuid && patientUuid == uuid
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

    private val WebSocketSession.uuid: String?
        get() = this.principal?.name

    private val WebSocketSession.webSocketPrincipal: WebSocketPrincipal?
        get() = this.principal as? WebSocketPrincipal

    //endregion

}