package com.edoctor.api.entities.network.model.user

import com.fasterxml.jackson.annotation.JsonProperty

sealed class UserModel {
    abstract val uuid: String
    abstract val fullName: String?
    abstract val city: String?
    abstract val dateOfBirthTimestamp: Long?
    abstract val isMale: Boolean?
    abstract val relativeImageUrl: String?
}

data class PatientModel(
        override val uuid: String,
        override val city: String?,
        override val fullName: String?,
        override val dateOfBirthTimestamp: Long?,
        @get:JsonProperty("isMale")
        override val isMale: Boolean?,
        override val relativeImageUrl: String?,
        val bloodGroup: Int?
) : UserModel()

data class DoctorModel(
        override val uuid: String,
        override val city: String?,
        override val fullName: String?,
        override val dateOfBirthTimestamp: Long?,
        @get:JsonProperty("isMale")
        override val isMale: Boolean?,
        override val relativeImageUrl: String?,
        val yearsOfExperience: Int?,
        val category: Int?,
        val specialization: String?,
        val clinicalInterests: String?,
        val education: String?,
        val workExperience: String?,
        val trainings: String?
) : UserModel()