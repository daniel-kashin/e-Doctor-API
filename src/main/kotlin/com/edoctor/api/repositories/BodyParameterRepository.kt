package com.edoctor.api.repositories

import com.edoctor.api.entities.storage.BodyParameterEntity
import com.edoctor.api.entities.storage.BodyParameterEntityType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BodyParameterRepository : JpaRepository<BodyParameterEntity, String> {

    @Query("SELECT new com.edoctor.api.entities.storage.BodyParameterEntityType(e.type, e.customModelName, e.customModelUnit) " +
            "FROM BodyParameterEntity e " +
            "WHERE e.patient.uuid = ?1 " +
            "GROUP BY e.type, e.customModelName, e.customModelUnit")
    fun getDistinctTypesForPatient(patientUuid: String): List<BodyParameterEntityType>

    fun findTopByTypeAndCustomModelNameAndCustomModelUnitAndPatientUuidOrderByMeasurementTimestampDesc(
            type: Int,
            customModelName: String?,
            customModelUnit: String?,
            patientUuid: String
    ): BodyParameterEntity?

    fun findAllByTypeAndCustomModelNameAndCustomModelUnitAndPatientUuid(
            type: Int,
            customModelName: String?,
            customModelUnit: String?,
            patientUuid: String
    ): List<BodyParameterEntity>

    fun findByUuidAndPatientUuid(
            uuid: String,
            patientUuid: String
    ): BodyParameterEntity?

    fun deleteByUuidAndPatientUuid(
            uuid: String,
            patientUuid: String
    )

}