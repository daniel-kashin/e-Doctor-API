package com.edoctor.api.entities.medicalrecord

abstract class BodyParameter : MedicalRecord()

abstract class DateSpecificBodyParameter : BodyParameter(), DateSpecific


data class Allergy(
        override val uuid: String,
        val name: String,
        val reaction: String?
) : BodyParameter()

data class BloodType(
        override val uuid: String,
        val group: Int,
        val isPositive: Boolean
) : BodyParameter() {

    init {
        require(group in 1..4)
    }

}


data class Height(
        override val uuid: String,
        override val timestamp: Long,
        val centimeters: Double
) : DateSpecificBodyParameter()

data class Weight(
        override val uuid: String,
        override val timestamp: Long,
        val kilos: Double
) : DateSpecificBodyParameter()

data class BloodSugar(
        override val uuid: String,
        override val timestamp: Long,
        val mmolPerLiter: Double
) : DateSpecificBodyParameter()

data class Temperature(
        override val uuid: String,
        override val timestamp: Long,
        val celsiusDegrees: Double
) : DateSpecificBodyParameter()

data class BloodOxygen(
        override val uuid: String,
        override val timestamp: Long,
        val percents: Int
) : DateSpecificBodyParameter()

data class CustomBodyParameter(
        override val uuid: String,
        override val timestamp: Long,
        val name: String,
        val value: String,
        val unit: String
) : DateSpecificBodyParameter()

data class BloodPressure(
        override val uuid: String,
        override val timestamp: Long,
        val systolicMmHg: Int,
        val diastolicMmHg: Int
) : DateSpecificBodyParameter()