package com.edoctor.api.mapper

import com.edoctor.api.entities.storage.DoctorEntity
import com.edoctor.api.entities.storage.PatientEntity
import com.edoctor.api.entities.network.response.UserResponse as NetworkUser

object UserMapper {

    fun toNetwork(user: PatientEntity): NetworkUser = user.run { NetworkUser(uuid, email, true) }

    fun toNetwork(user: DoctorEntity): NetworkUser = user.run { NetworkUser(uuid, email, false) }

}