package com.edoctor.api.entities.network.response

import com.edoctor.api.entities.network.model.record.MedicalEventWrapper

data class MedicalEventsResponse(
        val medicalEvents: List<MedicalEventWrapper>
)