package com.edoctor.api.entities.network.model.user

data class UserModelWrapper(
        val patientModel: PatientModel? = null,
        val doctorModel: DoctorModel? = null
)