package com.edoctor.api.entities.network.model.record

data class SynchronizeEventsModel(
    val events: List<MedicalEventWrapper>,
    val synchronizeTimestamp: Long
)