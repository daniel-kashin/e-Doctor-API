package com.edoctor.api.mapper

import com.edoctor.api.entities.network.response.DoctorResponse
import com.edoctor.api.entities.network.response.PatientResponse
import com.edoctor.api.entities.network.response.UserResponseWrapper
import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.entities.network.response.UserResponse as NetworkUser

object UserMapper {

    fun toNetwork(user: PatientEntity): UserResponseWrapper = user.run {
        UserResponseWrapper(
                patientResponse = PatientResponse(
                        email = email,
                        city = city,
                        fullName = fullName,
                        dateOfBirthTimestamp = dateOfBirthTimestamp,
                        isMale = isMale
                )
        )
    }

    fun toNetwork(user: DoctorEntity): UserResponseWrapper = user.run {
        UserResponseWrapper(
                doctorResponse = DoctorResponse(
                        email = email,
                        city = city,
                        fullName = fullName,
                        dateOfBirthTimestamp = dateOfBirthTimestamp,
                        isMale = isMale
                )
        )
    }

}