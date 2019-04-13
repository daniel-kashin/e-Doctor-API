package com.edoctor.api.mapper

import com.edoctor.api.entities.network.model.record.MedicalEventWrapper
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.MedicalEventEntity
import com.edoctor.api.entities.storage.PatientEntity
import java.util.*

object MedicalEventMapper {

    fun toNetwork(
            entity: MedicalEventEntity
    ): MedicalEventWrapper = entity.run {
        MedicalEventWrapper(uuid, timestamp, type, doctorCreator?.uuid, isAddedFromDoctor, endTimestamp, name, clinic, doctorName, doctorSpecialization, symptoms, diagnosis, recipe, comment)
    }

    fun toEntity(
            wrapper: MedicalEventWrapper,
            patient: PatientEntity,
            doctorCreator: DoctorEntity?
    ): MedicalEventEntity = wrapper.run {
        MedicalEventEntity(UUID.fromString(uuid), type, patient, doctorCreator, isAddedFromDoctor, timestamp, endTimestamp, name, clinic, doctorName, doctorSpecialization, symptoms, diagnosis, recipe, comment)
    }

}