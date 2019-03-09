package com.edoctor.api.entities.domain

data class CallActionRequest(val callAction: CallAction, val callUuid: String) {

    enum class CallAction {
        INITIATE,
        ENTER,
        LEAVE
    }

}

