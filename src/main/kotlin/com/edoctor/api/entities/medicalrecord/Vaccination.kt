package com.edoctor.api.entities.medicalrecord

data class Vaccination(
        override val uuid: String,
        override val timestamp: Long,
        override val clinic: String?,
        override val doctor: String?,
        override val comment: String?,
        val name: String
) : MedicalRecord(), DateSpecific, DoctorSpecific, ClinicSpecific, Commentable