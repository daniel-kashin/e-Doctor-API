package com.edoctor.api.entities.network.model.user

import com.fasterxml.jackson.annotation.JsonProperty

data class PatientModel(
        override val email: String,
        override val city: String?,
        override val fullName: String?,
        override val dateOfBirthTimestamp: Long?,
        @get:JsonProperty("isMale")
        override val isMale: Boolean?,
        override val relativeImageUrl: String?,
        val bloodGroup: Int?
) : UserModel()