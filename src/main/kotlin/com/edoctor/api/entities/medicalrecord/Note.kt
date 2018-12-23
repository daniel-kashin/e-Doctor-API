package com.edoctor.api.entities.medicalrecord

class Note(
        override val uuid: String,
        override val comment: String?,
        override val remindTimestamp: Long?
) : MedicalRecord(), Commentable, Remindable