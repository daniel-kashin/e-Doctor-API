package com.edoctor.api.entities.network.response

import com.edoctor.api.entities.network.model.record.BodyParameterWrapper

data class BodyParametersResponse(
        val bodyParameters: List<BodyParameterWrapper>
)