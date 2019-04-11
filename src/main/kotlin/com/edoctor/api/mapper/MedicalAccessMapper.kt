package com.edoctor.api.mapper

import com.edoctor.api.entities.network.model.record.MedicalAccessForDoctorModel
import com.edoctor.api.entities.network.model.record.MedicalAccessForPatientModel
import com.edoctor.api.entities.network.model.record.MedicalRecordType
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.MedicalAccessEntity
import com.edoctor.api.entities.storage.PatientEntity

object MedicalAccessMapper {

    fun toDoctorModel(
            patient: PatientEntity,
            medicalAccessesForPatient: List<MedicalAccessEntity>
    ) = MedicalAccessForDoctorModel(
            UserMapper.toModel(patient),
            medicalAccessesForPatient.map {
                MedicalRecordType(
                        it.medicalRecordType,
                        it.customModelName,
                        it.customModelUnit
                )
            }
    )

    fun toPatientModel(
            doctor: DoctorEntity,
            medicalAccessesForDoctor: List<MedicalAccessEntity>
    ) = MedicalAccessForPatientModel(
            UserMapper.toModel(doctor),
            medicalAccessesForDoctor.map {
                MedicalRecordType(
                        it.medicalRecordType,
                        it.customModelName,
                        it.customModelUnit
                )
            }
    )

}