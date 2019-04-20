package com.edoctor.api.mapper

import com.edoctor.api.controller.ImagesController.Companion.toRelativeImageUrl
import com.edoctor.api.entities.network.model.user.DoctorModel
import com.edoctor.api.entities.network.model.user.PatientModel
import com.edoctor.api.entities.network.model.user.UserModelWrapper
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.entities.network.model.user.UserModel as NetworkUser

object UserMapper {

    fun toModel(user: PatientEntity): PatientModel = user.run {
        PatientModel(
                uuid = uuid,
                city = city,
                fullName = fullName,
                dateOfBirthTimestamp = dateOfBirthTimestamp,
                isMale = isMale,
                relativeImageUrl = imageUuid?.let { toRelativeImageUrl(it) },
                bloodGroup = bloodGroup
        )
    }

    fun toModel(user: DoctorEntity): DoctorModel = user.run {
        DoctorModel(
                uuid = uuid,
                city = city,
                fullName = fullName,
                dateOfBirthTimestamp = dateOfBirthTimestamp,
                isMale = isMale,
                relativeImageUrl = imageUuid?.let { toRelativeImageUrl(it) },
                yearsOfExperience = yearsOfExperience,
                category = category,
                specialization = specialization,
                clinicalInterests = clinicalInterests,
                workExperience = workExperience,
                education = education,
                trainings = trainings
        )
    }

    fun toWrapper(user: PatientEntity): UserModelWrapper = UserModelWrapper(patientModel = toModel(user))

    fun toWrapper(user: DoctorEntity): UserModelWrapper = UserModelWrapper(doctorModel = toModel(user))


}