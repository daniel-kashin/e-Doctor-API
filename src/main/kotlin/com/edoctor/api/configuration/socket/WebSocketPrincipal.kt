package com.edoctor.api.configuration.socket

import java.security.Principal

data class WebSocketPrincipal(
        val uuid: String,
        val recipientUuid: String,
        val isPatient: Boolean
) : Principal {

    val patientUuid = if (isPatient) uuid else recipientUuid

    val doctorUuid = if (isPatient) recipientUuid else uuid

    override fun getName() = uuid

}