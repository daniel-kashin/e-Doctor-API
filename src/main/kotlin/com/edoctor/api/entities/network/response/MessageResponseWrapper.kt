package com.edoctor.api.entities.network.response

data class MessageResponseWrapper(
        val textMessageResponse: TextMessageResponse? = null,
        val callStatusMessageResponse: CallStatusMessageResponse? = null
)