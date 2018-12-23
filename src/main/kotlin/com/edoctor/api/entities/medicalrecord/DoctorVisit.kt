package com.edoctor.api.entities.medicalrecord

class DoctorVisit(
        override val uuid: String,
        override val clinic: String?,
        override val doctor: String?,
        override val comment: String?,
        override val remindTimestamp: Long?,
        val diagnosis: String?,
        val recipe: String?
) : MedicalRecord(), ClinicSpecific, DoctorSpecific, Commentable, Remindable