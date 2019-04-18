package com.edoctor.api.entities.network.model.record

data class BodyParameterWrapper(
        val uuid: String,
        val measurementTimestamp: Long,
        val updateTimestamp: Long,
        val isDeleted: Boolean,
        val type: Int,
        val firstValue: Double,
        val secondValue: Double? = null,
        val customModelName: String? = null,
        val customModelUnit: String? = null
)