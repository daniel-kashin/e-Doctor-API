package com.edoctor.api.mapper

import com.edoctor.api.entities.network.model.record.MedicalEventWrapper
import com.edoctor.api.entities.storage.MedicalEventEntity
import com.edoctor.api.entities.storage.PatientEntity
import java.util.*

object MedicalEventMapper {

    fun toNetwork(
            entity: MedicalEventEntity
    ): MedicalEventWrapper = entity.run {
        MedicalEventWrapper(uuid, timestamp, type, endTimestamp, name, clinic, doctorName, doctorSpecialization, symptoms, diagnosis, recipe, comment)
    }

    fun toEntity(
            wrapper: MedicalEventWrapper,
            patient: PatientEntity
    ): MedicalEventEntity = wrapper.run {
        MedicalEventEntity(UUID.fromString(uuid), type, patient, timestamp, endTimestamp, name, clinic, doctorName, doctorSpecialization, symptoms, diagnosis, recipe, comment)
    }

}