package com.edoctor.api.entities.network.response

import com.fasterxml.jackson.annotation.JsonProperty

data class DoctorResponse(
        override val email: String,
        override val city: String?,
        override val fullName: String?,
        override val dateOfBirthTimestamp: Long?,
        @get:JsonProperty("isMale")
        override val isMale: Boolean?,
        override val relativeImageUrl: String?,
        val yearsOfExperience: Int?,
        val category: Int?,
        val specialization: String?,
        val clinicalInterests: String?,
        val education: String?,
        val workExperience: String?,
        val trainings: String?
) : UserResponse()