package com.edoctor.api.entities.network.request

sealed class MessageRequest

data class CallActionMessageRequest(
    val callStatus: Int,
    val callUuid: String
): MessageRequest() {

    companion object {
        const val CALL_ACTION_INITIATE = 1
        const val CALL_ACTION_ENTER = 2
        const val CALL_ACTION_LEAVE = 3
    }

}


data class TextMessageRequest(
    val text: String
) : MessageRequest()