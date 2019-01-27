package com.edoctor.api.mapper

import com.edoctor.api.entities.network.UserResult
import com.edoctor.api.entities.storage.Doctor

object MessageMapper {

    fun toNetwork(user: Doctor): UserResult = user.run { UserResult(uuid, email, false) }

}