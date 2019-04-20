package com.edoctor.api.entities.network.model.record

data class SynchronizeBodyParametersModel(
        val bodyParameters: List<BodyParameterWrapper>,
        val synchronizeTimestamp: Long
)