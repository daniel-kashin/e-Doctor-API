package com.edoctor.api.entities.medicalrecord

// TODO: maybe use encapsulation instead of inheritance

abstract class MedicalRecord {
    abstract val uuid: String
}

interface DateSpecific {
    val timestamp: Long
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