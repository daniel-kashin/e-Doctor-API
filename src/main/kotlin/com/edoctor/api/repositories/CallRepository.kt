package com.edoctor.api.repositories

import org.springframework.stereotype.Repository
import com.edoctor.api.entities.domain.CallActionRequest
import com.edoctor.api.entities.domain.CallActionRequest.CallAction
import com.edoctor.api.entities.domain.CallActionRequest.CallAction.*
import com.edoctor.api.entities.domain.CallStatusResponse
import com.edoctor.api.entities.domain.CallStatusResponse.CallStatus
import com.edoctor.api.entities.domain.CallStatusResponse.CallStatus.*
import mu.KotlinLogging.logger

@Repository
class CallRepository {

    private val log = logger { }

    private val conversationsToCalls: MutableMap<Call, CallStatus> = hashMapOf()

    fun findActiveCall(
            patientUuid: String,
            doctorUuid: String
    ): Call? {
        log.info { "findActiveCall($patientUuid, $doctorUuid)" }

        return conversationsToCalls
                .entries
                .firstOrNull {
                    it.value != CANCELLED && it.key.patientUuid == patientUuid && it.key.doctorUuid == doctorUuid
                }
                ?.key
    }

    fun onCallActionRequest(
            call: Call,
            callAction: CallAction
    ) = onCallActionRequest(
            CallActionRequest(callAction, call.uuid),
            call.patientUuid,
            call.doctorUuid,
            call.isFromPatient
    ).also {
        log.info { "onCallActionRequest($callAction, $call)" }
    }

    fun onCallActionRequest(
            callActionRequest: CallActionRequest,
            patientUuid: String,
            doctorUuid: String,
            isPatient: Boolean
    ): CallStatusResponse? = synchronized(callActionRequest.callUuid) {
        val currentCallToInfo = conversationsToCalls.entries.firstOrNull { it.key.uuid == callActionRequest.callUuid }

        return when (callActionRequest.callAction) {
            CallAction.INITIATE -> {
                if (currentCallToInfo == null) {
                    val call = Call(callActionRequest.callUuid, patientUuid, doctorUuid, isPatient)
                    conversationsToCalls[call] = INITIATED
                    CallStatusResponse(INITIATED, call.uuid, call.senderUuid, call.recipientUuid, call.isFromPatient)
                            .also { log.info { "onResponse(request = $callActionRequest, response = $it)" } }
                } else {
                    log.info { "onResponse(request = $callActionRequest, response = null)" }
                    null
                }
            }
            ENTER -> {
                if (currentCallToInfo != null && currentCallToInfo.value == INITIATED) {
                    if (currentCallToInfo.key.isFromPatient != isPatient) {
                        val call = currentCallToInfo.key
                        conversationsToCalls[call] = STARTED
                        CallStatusResponse(STARTED, call.uuid, call.senderUuid, call.recipientUuid, call.isFromPatient)
                                .also { log.info { "onResponse(request = $callActionRequest, response = $it)" } }
                    } else {
                        log.info { "onResponse(request = $callActionRequest, response = null)" }
                        null
                    }
                } else {
                    log.info { "onResponse(request = $callActionRequest, response = null)" }
                    null
                }
            }
            LEAVE -> {
                if (currentCallToInfo != null && currentCallToInfo.value != CANCELLED) {
                    val call = currentCallToInfo.key
                    conversationsToCalls[call] = CANCELLED
                    CallStatusResponse(CANCELLED, call.uuid, call.senderUuid, call.recipientUuid, call.isFromPatient)
                            .also { log.info { "onResponse(request = $callActionRequest, response = $it)" } }
                } else {
                    log.info { "onResponse(request = $callActionRequest, response = null)" }
                    null
                }
            }
        }
    }

    data class Call(
            val uuid: String,
            val patientUuid: String,
            val doctorUuid: String,
            val isFromPatient: Boolean
    ) {
        val senderUuid: String = if (isFromPatient) patientUuid else doctorUuid
        val recipientUuid: String = if (isFromPatient) doctorUuid else patientUuid
    }

}