package com.edoctor.api.entities.network.response

import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse(
        val uuid: String,
        val email: String,
        @get:JsonProperty("isPatient") val isPatient: Boolean
)