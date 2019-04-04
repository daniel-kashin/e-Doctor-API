package com.edoctor.api.entities.network.model.record

// TODO: maybe use encapsulation instead of inheritance

abstract class MedicalRecordModel {
    abstract val uuid: String
}

interface DateSpecific {
    val measurementTimestamp: Long
}

interface ClinicSpecific {
    val clinic: String?
}

interface DoctorSpecific {
    val doctor: String?
}

interface Commentable {
    val comment: String?
}

interface DocumentAttachable {
    val documents: List<String>
}

interface Remindable {
    val remindTimestamp: Long?
}