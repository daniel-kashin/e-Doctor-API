package com.edoctor.api.entities.network.response

data class MessageResponseWrapper(
        val textMessageResponse: TextMessageResponse? = null,
        val callStatusMessageResponse: CallStatusMessageResponse? = null,
        val medicalAccessesMessageResponse: MedicalAccessesMessageResponse? = null,
        val medicalRecordRequestResponse: MedicalRecordRequestMessageResponse? = null,
        val imageMessageResponse: ImageMessageResponse? = null
)