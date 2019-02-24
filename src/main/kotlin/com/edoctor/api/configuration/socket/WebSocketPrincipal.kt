package com.edoctor.api.configuration.socket

import java.security.Principal

data class WebSocketPrincipal(
        val email: String,
        val uuid: String,
        val isPatient: Boolean
) : Principal {

    override fun getName() = email

}