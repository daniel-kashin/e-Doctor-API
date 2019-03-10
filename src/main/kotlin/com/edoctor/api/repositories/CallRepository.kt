package com.edoctor.api.repositories

import org.springframework.stereotype.Repository
import com.edoctor.api.entities.domain.CallActionRequest
import com.edoctor.api.entities.domain.CallActionRequest.CallAction
import com.edoctor.api.entities.domain.CallActionRequest.CallAction.*
import com.edoctor.api.entities.domain.CallStatusResponse
import com.edoctor.api.entities.domain.CallStatusResponse.CallStatus
import com.edoctor.api.entities.domain.CallStatusResponse.CallStatus.*
import mu.KotlinLogging.logger

// TODO: add call expiration
@Repository
class CallRepository {

    private val log = logger { }

    private val conversationsToCalls: MutableMap<Call, CallStatus> = hashMapOf()

    fun findActiveCall(
            patientEmail: String,
            doctorEmail: String
    ): Call? {
        log.info { "findActiveCall($patientEmail, $doctorEmail)" }

        return conversationsToCalls
                .entries
                .firstOrNull {
                    it.value != CANCELLED && it.key.patientEmail == patientEmail && it.key.doctorEmail == doctorEmail
                }
                ?.key
    }

    fun onCallActionRequest(
            call: Call,
            callAction: CallAction
    ) = onCallActionRequest(
            CallActionRequest(callAction, call.uuid),
            call.patientEmail,
            call.doctorEmail,
            call.isFromPatient
    ).also {
        log.info { "onCallActionRequest($callAction, $call)" }
    }

    fun onCallActionRequest(
            callActionRequest: CallActionRequest,
            patientEmail: String,
            doctorEmail: String,
            isPatient: Boolean
    ): CallStatusResponse? = synchronized(callActionRequest.callUuid) {
        val currentCallToInfo = conversationsToCalls.entries.firstOrNull { it.key.uuid == callActionRequest.callUuid }

        return when (callActionRequest.callAction) {
            CallAction.INITIATE -> {
                if (currentCallToInfo == null) {
                    val call = Call(callActionRequest.callUuid, patientEmail, doctorEmail, isPatient)
                    conversationsToCalls[call] = INITIATED
                    CallStatusResponse(INITIATED, call.uuid, call.senderEmail, call.recipientEmail, call.isFromPatient)
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
                        CallStatusResponse(STARTED, call.uuid, call.senderEmail, call.recipientEmail, call.isFromPatient)
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
                    CallStatusResponse(CANCELLED, call.uuid, call.senderEmail, call.recipientEmail, call.isFromPatient)
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
            val patientEmail: String,
            val doctorEmail: String,
            val isFromPatient: Boolean
    ) {
        val senderEmail: String = if (isFromPatient) patientEmail else doctorEmail
        val recipientEmail: String = if (isFromPatient) doctorEmail else patientEmail
    }

}