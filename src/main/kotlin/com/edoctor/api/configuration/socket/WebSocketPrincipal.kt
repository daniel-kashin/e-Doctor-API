package com.edoctor.api.configuration.socket

import java.security.Principal

data class WebSocketPrincipal(
        val email: String,
        val recipientEmail: String,
        val isPatient: Boolean
) : Principal {

    val patientEmail = if (isPatient) email else recipientEmail

    val doctorEmail = if (isPatient) recipientEmail else email

    override fun getName() = email

}