package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.MedicalAccessEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MedicalAccessesRepository : JpaRepository<MedicalAccessEntity, String> {

    fun findAllByDoctorUuid(doctorUuid: String): List<MedicalAccessEntity>

    fun findAllByPatientUuid(patientUuid: String): List<MedicalAccessEntity>

    fun removeAllByDoctorUuidAndPatientUuid(doctorUuid: String, patientUuid: String)

}