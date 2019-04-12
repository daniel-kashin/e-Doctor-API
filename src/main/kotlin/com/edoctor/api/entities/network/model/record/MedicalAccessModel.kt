package com.edoctor.api.entities.network.model.record

import com.edoctor.api.entities.network.model.user.DoctorModel
import com.edoctor.api.entities.network.model.user.PatientModel

data class MedicalAccessesForDoctorModel(
        val medicalAccesses: List<MedicalAccessForDoctorModel>
)

data class MedicalAccessesForPatientModel(
        val medicalAccesses: List<MedicalAccessForPatientModel>,
        val allTypes: List<MedicalRecordTypeModel>
)

data class MedicalAccessForDoctorModel(
        val patient: PatientModel,
        val availableTypes: List<MedicalRecordTypeModel>,
        val allTypes: List<MedicalRecordTypeModel>
)

data class MedicalAccessForPatientModel(
        val doctor: DoctorModel,
        val availableTypes: List<MedicalRecordTypeModel>
)

data class MedicalRecordTypeModel(
        val medicalRecordType: Int,
        val customModelName: String? = null,
        val customModelUnit: String? = null
)