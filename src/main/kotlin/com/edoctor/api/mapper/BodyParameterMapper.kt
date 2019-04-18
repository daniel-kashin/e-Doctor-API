package com.edoctor.api.mapper

import com.edoctor.api.entities.network.model.record.BodyParameterWrapper
import com.edoctor.api.entities.storage.BodyParameterEntity
import com.edoctor.api.entities.storage.PatientEntity
import java.util.*

object BodyParameterMapper {

    fun toNetwork(entity: BodyParameterEntity): BodyParameterWrapper = entity.run {
        BodyParameterWrapper(uuid, measurementTimestamp, updateTimestamp, isDeleted, type, firstValue, secondValue, customModelName, customModelUnit)
    }

    fun toEntityFromWrapper(
            wrapper: BodyParameterWrapper,
            patientEntity: PatientEntity
    ): BodyParameterEntity = wrapper.run {
        BodyParameterEntity(UUID.fromString(uuid), type, patientEntity, updateTimestamp, isDeleted, measurementTimestamp, firstValue, secondValue, customModelName, customModelUnit)
    }

}