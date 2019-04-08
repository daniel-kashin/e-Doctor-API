package com.edoctor.api.mapper

import com.edoctor.api.entities.network.model.record.BodyParameterWrapper
import com.edoctor.api.entities.storage.BodyParameterEntity
import java.util.*

object BodyParameterMapper {

    fun toNetwork(entity: BodyParameterEntity): BodyParameterWrapper = entity.run {
        BodyParameterWrapper(uuid, measurementTimestamp, type, firstValue, secondValue, customModelName, customModelUnit)
    }

    fun toEntity(wrapper: BodyParameterWrapper): BodyParameterEntity = wrapper.run {
        BodyParameterEntity(UUID.fromString(uuid), measurementTimestamp, type, firstValue, secondValue, customModelName, customModelUnit)
    }

}