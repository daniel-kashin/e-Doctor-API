package com.edoctor.api.entities.network.model.record

class DoctorVisit(
        override val uuid: String,
        override val clinic: String?,
        override val doctor: String?,
        override val comment: String?,
        override val remindTimestamp: Long?,
        val diagnosis: String?,
        val recipe: String?
) : MedicalRecordModel(), ClinicSpecific, DoctorSpecific, Commentable, Remindable