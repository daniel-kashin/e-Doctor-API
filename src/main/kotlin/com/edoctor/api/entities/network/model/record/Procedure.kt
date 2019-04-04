package com.edoctor.api.entities.network.model.record

data class Procedure(
        override val uuid: String,
        override val measurementTimestamp: Long,
        override val clinic: String?,
        override val comment: String?,
        val name: String
) : MedicalRecordModel(), DateSpecific, ClinicSpecific, Commentable