package com.edoctor.api.entities.network.request

data class MessageRequestWrapper(
        val callActionMessageRequest: CallActionMessageRequest? = null,
        val textMessageRequest: TextMessageRequest? = null
)