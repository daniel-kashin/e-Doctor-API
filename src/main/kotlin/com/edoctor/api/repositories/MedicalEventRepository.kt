package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.MedicalEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MedicalEventRepository : JpaRepository<MedicalEventEntity, String> {

    fun findByUuidAndPatientUuid(uuid: String, patientUuid: String): MedicalEventEntity?

    fun deleteByUuidAndPatientUuid(uuid: String, patientUuid: String)

}