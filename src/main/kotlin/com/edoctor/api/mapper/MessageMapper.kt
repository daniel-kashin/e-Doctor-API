package com.edoctor.api.mapper

import com.edoctor.api.controller.ImagesController.Companion.toRelativeImageUrl
import com.edoctor.api.entities.domain.CallActionRequest
import com.edoctor.api.entities.domain.CallActionRequest.CallAction.*
import com.edoctor.api.entities.domain.CallStatusResponse
import com.edoctor.api.entities.network.request.CallActionMessageRequest
import com.edoctor.api.entities.network.request.CallActionMessageRequest.Companion.CALL_ACTION_ENTER
import com.edoctor.api.entities.network.request.CallActionMessageRequest.Companion.CALL_ACTION_INITIATE
import com.edoctor.api.entities.network.request.CallActionMessageRequest.Companion.CALL_ACTION_LEAVE
import com.edoctor.api.entities.network.request.MessageRequest
import com.edoctor.api.entities.network.request.MessageRequestWrapper
import com.edoctor.api.entities.network.request.TextMessageRequest
import com.edoctor.api.entities.network.response.*
import com.edoctor.api.entities.network.response.CallStatusMessageResponse.Companion.CALL_STATUS_CANCELLED
import com.edoctor.api.entities.network.response.CallStatusMessageResponse.Companion.CALL_STATUS_INITIATED
import com.edoctor.api.entities.network.response.CallStatusMessageResponse.Companion.CALL_STATUS_STARTED
import com.edoctor.api.entities.storage.ConversationEntity
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.MessageEntity
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.mapper.UserMapper.toWrapper
import com.edoctor.api.utils.currentUnixTime
import java.lang.IllegalStateException
import java.util.UUID.randomUUID

object MessageMapper {

    private const val MESSAGE_TYPE_MEDICAL_ACCESSES = 0
    private const val MESSAGE_TYPE_MEDICAL_RECORD_REQUEST = 1
    private const val MESSAGE_TYPE_TEXT = 2
    private const val MESSAGE_TYPE_CALL_STATUS = 3
    private const val MESSAGE_TYPE_IMAGE = 4

    fun unwrapRequest(
            messageRequestWrapper: MessageRequestWrapper
    ): MessageRequest? = when {
        messageRequestWrapper.textMessageRequest != null -> messageRequestWrapper.textMessageRequest
        messageRequestWrapper.callActionMessageRequest != null -> messageRequestWrapper.callActionMessageRequest
        else -> null
    }

    fun wrapResponse(
            messageResponse: MessageResponse?
    ): MessageResponseWrapper? = when (messageResponse) {
        is TextMessageResponse -> MessageResponseWrapper(textMessageResponse = messageResponse)
        is CallStatusMessageResponse -> MessageResponseWrapper(callStatusMessageResponse = messageResponse)
        is MedicalRecordRequestMessageResponse -> MessageResponseWrapper(medicalRecordRequestResponse = messageResponse)
        is MedicalAccessesMessageResponse -> MessageResponseWrapper(medicalAccessesMessageResponse = messageResponse)
        is ImageMessageResponse -> MessageResponseWrapper(imageMessageResponse = messageResponse)
        else -> null
    }

    fun toDomainCallAction(
            callActionMessageRequest: CallActionMessageRequest
    ): CallActionRequest = callActionMessageRequest.run {
        val (callAction, callUuid) = when (callStatus) {
            CALL_ACTION_INITIATE -> INITIATE to randomUUID().toString()
            CALL_ACTION_ENTER -> ENTER to callUuid
            CALL_ACTION_LEAVE -> LEAVE to callUuid
            else -> throw IllegalStateException()
        }

        return CallActionRequest(callAction, callUuid)
    }

    fun toResponse(
            messageEntity: MessageEntity,
            patientEntity: PatientEntity,
            doctorEntity: DoctorEntity
    ): MessageResponse? = messageEntity.run {
        val sender = if (isFromPatient) toWrapper(patientEntity) else toWrapper(doctorEntity)
        val recipient = if (isFromPatient) toWrapper(doctorEntity) else toWrapper(patientEntity)

        return when {
            type == MESSAGE_TYPE_TEXT && text != null -> {
                TextMessageResponse(uuid, sender, recipient, timestamp, text)
            }
            type == MESSAGE_TYPE_CALL_STATUS && callStatus != null && callUuid != null -> {
                CallStatusMessageResponse(uuid, sender, recipient, timestamp, callStatus, callUuid)
            }
            type == MESSAGE_TYPE_MEDICAL_RECORD_REQUEST -> {
                MedicalRecordRequestMessageResponse(uuid, sender, recipient, timestamp)
            }
            type == MESSAGE_TYPE_MEDICAL_ACCESSES -> {
                MedicalAccessesMessageResponse(uuid, sender, recipient, timestamp)
            }
            type == MESSAGE_TYPE_IMAGE && imageUuid != null -> {
                ImageMessageResponse(uuid, sender, recipient, timestamp, toRelativeImageUrl(imageUuid))
            }
            else -> null
        }
    }

    fun toEntityImage(
            imageUuid: String,
            isFromPatient: Boolean,
            conversation: ConversationEntity
    ) : MessageEntity = MessageEntity(
            givenUuid = randomUUID(),
            timestamp = currentUnixTime(),
            type = MESSAGE_TYPE_IMAGE,
            imageUuid = imageUuid,
            isFromPatient = isFromPatient,
            conversation = conversation
    )

    fun toEntityMedicalAccesses(
            conversation: ConversationEntity
    ): MessageEntity = MessageEntity(
            givenUuid = randomUUID(),
            timestamp = currentUnixTime(),
            type = MESSAGE_TYPE_MEDICAL_ACCESSES,
            isFromPatient = true,
            conversation = conversation
    )

    fun toEntityMedicalRecordRequest(
            conversation: ConversationEntity
    ): MessageEntity = MessageEntity(
            givenUuid = randomUUID(),
            timestamp = currentUnixTime(),
            type = MESSAGE_TYPE_MEDICAL_RECORD_REQUEST,
            isFromPatient = false,
            conversation = conversation
    )

    fun toEntityText(
            textMessageResult: TextMessageRequest,
            isFromPatient: Boolean,
            conversation: ConversationEntity
    ): MessageEntity = textMessageResult.run {
        MessageEntity(
                givenUuid = randomUUID(),
                timestamp = currentUnixTime(),
                type = MESSAGE_TYPE_TEXT,
                text = text,
                isFromPatient = isFromPatient,
                conversation = conversation
        )
    }

    fun toEntityCallAction(
            callActionStatusResponse: CallStatusResponse,
            conversation: ConversationEntity
    ): MessageEntity = callActionStatusResponse.run {
        val status = when (callStatus) {
            CallStatusResponse.CallStatus.INITIATED -> CALL_STATUS_INITIATED
            CallStatusResponse.CallStatus.STARTED -> CALL_STATUS_STARTED
            CallStatusResponse.CallStatus.CANCELLED -> CALL_STATUS_CANCELLED
        }

        MessageEntity(
                givenUuid = randomUUID(),
                timestamp = currentUnixTime(),
                type = MESSAGE_TYPE_CALL_STATUS,
                callStatus = status,
                callUuid = callUuid,
                isFromPatient = isFromPatient,
                conversation = conversation
        )
    }

}