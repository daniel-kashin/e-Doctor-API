package com.edoctor.api.mapper

import com.edoctor.api.entities.domain.BodyParameterType
import com.edoctor.api.entities.domain.MedicalEventType
import com.edoctor.api.entities.domain.MedicalRecordType
import com.edoctor.api.entities.network.model.record.MedicalRecordTypeModel
import com.edoctor.api.entities.storage.*

object MedicalRecordTypeMapper {

    private const val BODY_PARAMETERS_OFFSET = 0
    private const val MEDICAL_EVENTS_OFFSET = 10000

    const val BODY_PARAMETER_TYPE_CUSTOM = 0
    const val BODY_PARAMETER_TYPE_HEIGHT = 1
    const val BODY_PARAMETER_TYPE_WEIGHT = 2
    const val BODY_PARAMETER_TYPE_BLOOD_PRESSURE = 3
    const val BODY_PARAMETER_TYPE_BLOOD_SUGAR = 4
    const val BODY_PARAMETER_TYPE_BLOOD_OXYGEN = 5
    const val BODY_PARAMETER_TYPE_TEMPERATURE = 6

    const val MEDICAL_EVENT_TYPE_ANALYSIS = 0
    const val MEDICAL_EVENT_TYPE_ALLERGY = 1
    const val MEDICAL_EVENT_TYPE_NOTE = 2
    const val MEDICAL_EVENT_TYPE_VACCINATION = 3
    const val MEDICAL_EVENT_TYPE_PROCEDURE = 4
    const val MEDICAL_EVENT_TYPE_DOCTOR_VISIT = 5
    const val MEDICAL_EVENT_TYPE_SICKNESS = 6

    fun toDomain(entityType: BodyParameterEntityType): BodyParameterType? =
            when (entityType.type) {
                BODY_PARAMETER_TYPE_HEIGHT -> BodyParameterType.Height
                BODY_PARAMETER_TYPE_WEIGHT -> BodyParameterType.Weight
                BODY_PARAMETER_TYPE_BLOOD_PRESSURE -> BodyParameterType.BloodPressure
                BODY_PARAMETER_TYPE_BLOOD_SUGAR -> BodyParameterType.BloodSugar
                BODY_PARAMETER_TYPE_BLOOD_OXYGEN -> BodyParameterType.BloodOxygen
                BODY_PARAMETER_TYPE_TEMPERATURE -> BodyParameterType.Temperature
                BODY_PARAMETER_TYPE_CUSTOM -> {
                    if (entityType.customModelName != null && entityType.customModelUnit != null) {
                        BodyParameterType.Custom(entityType.customModelName, entityType.customModelUnit)
                    } else {
                        null
                    }
                }
                else -> null
            }

    fun toDomain(entityType: MedicalEventEntityType): MedicalEventType? =
            when (entityType.type) {
                MEDICAL_EVENT_TYPE_ANALYSIS -> MedicalEventType.Analysis
                MEDICAL_EVENT_TYPE_ALLERGY -> MedicalEventType.Allergy
                MEDICAL_EVENT_TYPE_NOTE -> MedicalEventType.Note
                MEDICAL_EVENT_TYPE_VACCINATION -> MedicalEventType.Vaccination
                MEDICAL_EVENT_TYPE_PROCEDURE -> MedicalEventType.Procedure
                MEDICAL_EVENT_TYPE_DOCTOR_VISIT -> MedicalEventType.DoctorVisit
                MEDICAL_EVENT_TYPE_SICKNESS -> MedicalEventType.Sickness
                else -> null
            }

    fun toModel(domain: MedicalRecordType): MedicalRecordTypeModel =
            when (domain) {
                is BodyParameterType -> when (domain) {
                    is BodyParameterType.Height -> {
                        MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_HEIGHT)
                    }
                    is BodyParameterType.Weight -> {
                        MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_WEIGHT)
                    }
                    is BodyParameterType.BloodPressure -> {
                        MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_BLOOD_PRESSURE)
                    }
                    is BodyParameterType.BloodSugar -> {
                        MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_BLOOD_SUGAR)
                    }
                    is BodyParameterType.BloodOxygen -> {
                        MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_BLOOD_OXYGEN)
                    }
                    is BodyParameterType.Temperature -> {
                        MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_TEMPERATURE)
                    }
                    is BodyParameterType.Custom -> {
                        MedicalRecordTypeModel(
                                BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_CUSTOM,
                                domain.name,
                                domain.unit
                        )
                    }
                }
                is MedicalEventType -> when (domain) {
                    is MedicalEventType.Analysis -> {
                        MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_ANALYSIS)
                    }
                    is MedicalEventType.Allergy -> {
                        MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_ALLERGY)
                    }
                    is MedicalEventType.Note -> {
                        MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_NOTE)
                    }
                    is MedicalEventType.Vaccination -> {
                        MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_VACCINATION)
                    }
                    is MedicalEventType.Procedure -> {
                        MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_PROCEDURE)
                    }
                    is MedicalEventType.DoctorVisit -> {
                        MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_DOCTOR_VISIT)
                    }
                    is MedicalEventType.Sickness -> {
                        MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_SICKNESS)
                    }
                }
            }

}