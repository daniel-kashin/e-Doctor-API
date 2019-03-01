package com.edoctor.api.mapper

import com.edoctor.api.entities.network.TextMessage
import com.edoctor.api.entities.network.UserResult
import com.edoctor.api.entities.storage.Doctor
import com.edoctor.api.entities.storage.Message

object MessageMapper {

    fun toNetwork(message: Message, patientEmail: String, doctorEmail: String): TextMessage {
        val senderEmail = if (message.isFromPatient) patientEmail else doctorEmail
        val recipientEmail = if (message.isFromPatient) doctorEmail else patientEmail
        return TextMessage(message.uuid, senderEmail, recipientEmail, message.timestamp, message.text)
    }

}