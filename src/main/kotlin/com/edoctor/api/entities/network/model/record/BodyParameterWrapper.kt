package com.edoctor.api.entities.network.model.record

import com.fasterxml.jackson.annotation.JsonProperty

data class BodyParameterWrapper(
        val uuid: String,
        val measurementTimestamp: Long,
        @get:JsonProperty("isDeleted")
        val isDeleted: Boolean,
        val type: Int,
        val firstValue: Double,
        val secondValue: Double? = null,
        val customModelName: String? = null,
        val customModelUnit: String? = null
)