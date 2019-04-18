package com.edoctor.api.entities.network.request

import com.edoctor.api.entities.network.model.record.BodyParameterWrapper

data class SynchronizeBodyParametersModel(
        val bodyParameters: List<BodyParameterWrapper>,
        val synchronizeTimestamp: Long
)