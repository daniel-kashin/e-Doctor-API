package com.edoctor.api.entities.network.response

data class DoctorResponse(
        override val email: String,
        override val city: String?,
        override val fullName: String?,
        override val dateOfBirthTimestamp: Long?
) : UserResponse()