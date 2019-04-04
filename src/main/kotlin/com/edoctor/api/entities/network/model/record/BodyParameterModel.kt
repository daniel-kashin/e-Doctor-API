package com.edoctor.api.entities.network.model.record

sealed class BodyParameterModel : MedicalRecordModel(), DateSpecific

data class HeightModel(
        override val uuid: String,
        override val measurementTimestamp: Long,
        val centimeters: Double
) : BodyParameterModel()

data class WeightModel(
        override val uuid: String,
        override val measurementTimestamp: Long,
        val kilograms: Double
) : BodyParameterModel()

data class BloodPressureModel(
        override val uuid: String,
        override val measurementTimestamp: Long,
        val systolicMmHg: Int,
        val diastolicMmHg: Int
) : BodyParameterModel()

data class BloodSugarModel(
        override val uuid: String,
        override val measurementTimestamp: Long,
        val mmolPerLiter: Double
) : BodyParameterModel()

data class TemperatureModel(
        override val uuid: String,
        override val measurementTimestamp: Long,
        val celsiusDegrees: Double
) : BodyParameterModel()

data class BloodOxygenModel(
        override val uuid: String,
        override val measurementTimestamp: Long,
        val percents: Int
) : BodyParameterModel()

data class CustomBodyParameterModel(
        override val uuid: String,
        override val measurementTimestamp: Long,
        val name: String,
        val value: String,
        val unit: String
) : BodyParameterModel()