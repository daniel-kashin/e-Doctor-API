package com.edoctor.api.entities.network.model.record

class Note(
        override val uuid: String,
        override val comment: String?,
        override val remindTimestamp: Long?
) : MedicalRecordModel(), Commentable, Remindable