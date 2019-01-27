package com.edoctor.api.mapper

import com.edoctor.api.entities.storage.Doctor
import com.edoctor.api.entities.storage.Patient
import com.edoctor.api.entities.network.UserResult as NetworkUser

object UserMapper {

    fun toNetwork(user: Patient): NetworkUser = user.run { NetworkUser(uuid, email, true) }

    fun toNetwork(user: Doctor): NetworkUser = user.run { NetworkUser(uuid, email, false) }

}