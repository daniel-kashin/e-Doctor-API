package com.edoctor.api.entities.medicalrecord

class Sickness(
        override val comment: String?,
        val startTimestamp: Long?,
        val endTimestamp: Long?,
        val diagnosis: String,
        val symptoms: String?
) : Commentable