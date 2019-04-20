package com.edoctor.api.mapper

import com.edoctor.api.entities.network.model.record.MedicalEventWrapper
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.MedicalEventEntity
import com.edoctor.api.entities.storage.PatientEntity
import java.util.*

object MedicalEventMapper {

    fun toWrapperFromEntity(
            entity: MedicalEventEntity
    ): MedicalEventWrapper = entity.run {
        MedicalEventWrapper(uuid, timestamp, type, doctorCreator?.uuid, isDeleted, isAddedFromDoctor, endTimestamp, name, clinic, doctorName, doctorSpecialization, symptoms, diagnosis, recipe, comment)
    }

    fun toEntityFromWrapper(
            wrapper: MedicalEventWrapper,
            patient: PatientEntity,
            doctorCreator: DoctorEntity?,
            updateTimestamp: Long
    ): MedicalEventEntity = wrapper.run {
        MedicalEventEntity(UUID.fromString(uuid), type, patient, isDeleted, updateTimestamp, doctorCreator, isAddedFromDoctor, timestamp, endTimestamp, name, clinic, doctorName, doctorSpecialization, symptoms, diagnosis, recipe, comment)
    }

}