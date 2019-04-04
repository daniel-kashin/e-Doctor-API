package com.edoctor.api.entities.network.model.record

data class Vaccination(
        override val uuid: String,
        override val measurementTimestamp: Long,
        override val clinic: String?,
        override val doctor: String?,
        override val comment: String?,
        val name: String
) : MedicalRecordModel(), DateSpecific, DoctorSpecific, ClinicSpecific, Commentable