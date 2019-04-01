package com.edoctor.api.entities.network.response

data class UserResponseWrapper(
        val patientResponse: PatientResponse? = null,
        val doctorResponse: DoctorResponse? = null
)