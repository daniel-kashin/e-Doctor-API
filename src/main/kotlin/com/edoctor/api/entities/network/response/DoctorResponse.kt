package com.edoctor.api.entities.network.response

import com.fasterxml.jackson.annotation.JsonProperty

data class DoctorResponse(
        override val email: String,
        override val city: String?,
        override val fullName: String?,
        override val dateOfBirthTimestamp: Long?,
        @get:JsonProperty("isMale")
        override val isMale: Boolean?
) : UserResponse()