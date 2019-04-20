package com.edoctor.api.entities.network.model.record

import com.fasterxml.jackson.annotation.JsonProperty

data class MedicalEventWrapper(
        val uuid: String,
        val timestamp: Long,
        val type: Int,
        val doctorCreatorUuid: String?,
        @get:JsonProperty("isDeleted")
        val isDeleted: Boolean,
        @get:JsonProperty("isAddedFromDoctor")
        val isAddedFromDoctor: Boolean,
        val endTimestamp: Long? = null,
        val name: String? = null,
        val clinic: String? = null,
        val doctorName: String? = null,
        val doctorSpecialization: String? = null,
        val symptoms: String? = null,
        val diagnosis: String? = null,
        val recipe: String? = null,
        val comment: String? = null
)