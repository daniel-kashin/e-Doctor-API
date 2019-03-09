package com.edoctor.api.entities.domain

data class CallStatusResponse(
        val callStatus: CallStatus,
        val callUuid: String,
        val senderEmail: String,
        val recipientEmail: String,
        val isFromPatient: Boolean
) {

    enum class CallStatus {
        INITIATED,
        STARTED,
        CANCELLED
    }

}

