package com.edoctor.api.entities.network.model.record

data class Analysis(
        override val uuid: String,
        override val measurementTimestamp: Long,
        override val clinic: String?,
        val name: String,
        val value: String,
        val unit: String,
        val norm: String
) : MedicalRecordModel(), DateSpecific, ClinicSpecific