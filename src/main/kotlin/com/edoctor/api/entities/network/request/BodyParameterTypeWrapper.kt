package com.edoctor.api.entities.network.request

data class BodyParameterTypeWrapper(
        val type: Int,
        val customModelName: String? = null,
        val customModelUnit: String? = null
)