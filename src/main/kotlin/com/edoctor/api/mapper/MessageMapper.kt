package com.edoctor.api.mapper

import com.edoctor.api.entities.network.TextMessage
import com.edoctor.api.entities.network.User
import com.edoctor.api.entities.storage.Doctor
import com.edoctor.api.entities.storage.Message
import com.edoctor.api.entities.storage.Patient

object MessageMapper {

    fun toNetwork(user: Doctor): User = user.run { User(uuid, email, false) }

}