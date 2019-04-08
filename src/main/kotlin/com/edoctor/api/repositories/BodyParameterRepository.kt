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
            "GROUP BY e.type, e.customModelName, e.customModelUnit")
    fun getDistinctTypes(): List<BodyParameterEntityType>


    fun findTopByTypeAndCustomModelNameAndCustomModelUnitOrderByMeasurementTimestampDesc(
            type: Int,
            customModelName: String?,
            customModelUnit: String?
    ): BodyParameterEntity?

    fun findAllByTypeAndCustomModelNameAndCustomModelUnit(
            type: Int,
            customModelName: String?,
            customModelUnit: String?
    ): List<BodyParameterEntity>

}