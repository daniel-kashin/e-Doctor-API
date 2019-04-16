package com.edoctor.api.entities.domain

data class CallStatusResponse(
        val callStatus: CallStatus,
        val callUuid: String,
        val senderUuid: String,
        val recipientUuid: String,
        val isFromPatient: Boolean
) {

    enum class CallStatus {
        INITIATED,
        STARTED,
        CANCELLED
    }

}

