package com.edoctor.api.entities.network.response

data class PatientResponse(
        override val city: String?,
        override val email: String,
        override val fullName: String?
): UserResponse()