package com.edoctor.api.entities.network.response

import com.fasterxml.jackson.annotation.JsonProperty

data class PatientResponse(
        override val email: String,
        override val city: String?,
        override val fullName: String?,
        override val dateOfBirthTimestamp: Long?,
        @get:JsonProperty("isMale")
        override val isMale: Boolean?,
        override val relativeImageUrl: String?
) : UserResponse()