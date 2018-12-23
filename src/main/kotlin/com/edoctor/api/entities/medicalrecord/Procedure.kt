package com.edoctor.api.entities.medicalrecord

data class Procedure(
        override val uuid: String,
        override val timestamp: Long,
        override val clinic: String?,
        override val comment: String?,
        val name: String
) : MedicalRecord(), DateSpecific, ClinicSpecific, Commentable