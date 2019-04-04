package com.edoctor.api.entities.network.model.record

class Sickness(
        override val comment: String?,
        val startTimestamp: Long?,
        val endTimestamp: Long?,
        val diagnosis: String,
        val symptoms: String?
) : Commentable