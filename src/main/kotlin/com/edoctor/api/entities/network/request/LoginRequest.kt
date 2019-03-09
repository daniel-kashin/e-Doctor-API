package com.edoctor.api.entities.network.request

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest(
        val email: String,
        val password: String,
        @get:JsonProperty("isPatient") val isPatient: Boolean
)