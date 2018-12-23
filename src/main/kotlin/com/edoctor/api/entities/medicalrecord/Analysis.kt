package com.edoctor.api.entities.medicalrecord

data class Analysis(
        override val uuid: String,
        override val timestamp: Long,
        override val clinic: String?,
        val name: String,
        val value: String,
        val unit: String,
        val norm: String
) : MedicalRecord(), DateSpecific, ClinicSpecific