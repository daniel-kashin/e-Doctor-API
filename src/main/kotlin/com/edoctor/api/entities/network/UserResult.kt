package com.edoctor.api.entities.network

import com.fasterxml.jackson.annotation.JsonProperty

data class UserResult(
        val uuid: String,
        val email: String,
        @get:JsonProperty("isPatient") val isPatient: Boolean
)