package com.edoctor.api.mapper

import com.edoctor.api.entities.network.model.user.DoctorModel
import com.edoctor.api.entities.network.model.user.PatientModel
import com.edoctor.api.entities.network.model.user.UserModelWrapper
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.entities.network.model.user.UserModel as NetworkUser

object UserMapper {

    fun toNetwork(user: PatientEntity): UserModelWrapper = user.run {
        UserModelWrapper(
                patientModel = PatientModel(
                        email = email,
                        city = city,
                        fullName = fullName,
                        dateOfBirthTimestamp = dateOfBirthTimestamp,
                        isMale = isMale,
                        relativeImageUrl = toRelativeImageUrl(imageUuid),
                        bloodGroup = bloodGroup
                )
        )
    }

    fun toNetwork(user: DoctorEntity): UserModelWrapper = user.run {
        UserModelWrapper(
                doctorModel = DoctorModel(
                        email = email,
                        city = city,
                        fullName = fullName,
                        dateOfBirthTimestamp = dateOfBirthTimestamp,
                        isMale = isMale,
                        relativeImageUrl = toRelativeImageUrl(imageUuid),
                        yearsOfExperience = yearsOfExperience,
                        category = category,
                        specialization = specialization,
                        clinicalInterests = clinicalInterests,
                        workExperience = workExperience,
                        education = education,
                        trainings = trainings
                )
        )
    }

    private fun toRelativeImageUrl(imageUuid: String?): String? = imageUuid?.let { "/images/$it" }

}